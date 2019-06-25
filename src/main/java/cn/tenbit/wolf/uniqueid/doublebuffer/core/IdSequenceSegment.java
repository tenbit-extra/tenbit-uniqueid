package cn.tenbit.wolf.uniqueid.doublebuffer.core;

import cn.tenbit.hare.core.lite.exception.HareException;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author bangquan.qian
 * @Date 2019-05-18 17:58
 */

public class IdSequenceSegment {

    // 15min
    private static final long SEGMENT_DURATION_MS = 15 * 60 * 1000L;

    private static final int MAX_STEP = 100_0000;

    private static final double THRESHOLD = 0.75;

    //--------------------------------------------------------------------------------------------------//

    private IdSequenceQueue queue;

    private volatile int dbMinStep;

    private volatile long dbStartId;

    private volatile long nextStartId;

    private volatile int currentStep;

    private volatile long lastUpdateTime;

    private final AtomicLong value = new AtomicLong();

    private final AtomicBoolean initStatus = new AtomicBoolean(false);

    private final Lock initLock = new ReentrantLock();

    private final ReadWriteLock valueLock = new ReentrantReadWriteLock();

    private final Lock queueLock = new ReentrantLock();

    private final AtomicLong updateCapacityTimes = new AtomicLong(0);

    IdSequenceSegment(IdSequenceQueue queue) {
        this.queue = queue;
    }

    //--------------------------------------------------------------------------------------------------//

    public void init(long nextStartId, int dbMinStep) {
        if (getInitStatus()) {
            return;
        }
        try {
            initLock.lock();
            if (getInitStatus()) {
                return;
            }
            doInit(nextStartId, dbMinStep);
            initStatus.compareAndSet(false, true);
        } finally {
            initLock.unlock();
        }
    }

    private void doInit(long nextStartId, int dbMinStep) {
        setDbStartId(calcCurrentId(nextStartId, dbMinStep));
        setDbMinStep(dbMinStep);

        setCurrentId(calcCurrentId(nextStartId, dbMinStep));
        setCurrentStep(dbMinStep);

        setNextStartId(nextStartId);
        refreshLastUpdateTime();
    }

    //--------------------------------------------------------------------------------------------------//

    private long getDbStartId() {
        return dbStartId;
    }

    private int getDbMinStep() {
        return dbMinStep;
    }

    public boolean getInitStatus() {
        return initStatus.get();
    }

    private long getCurrentId() {
        return value.get();
    }

    private long getNextStartId() {
        return nextStartId;
    }

    private int getCurrentStep() {
        return currentStep;
    }

    private long getLastUpdateTime() {
        return lastUpdateTime;
    }

    Lock getQueueLock() {
        return queueLock;
    }

    public IdSequenceQueue getQueue() {
        return queue;
    }

    //--------------------------------------------------------------------------------------------------//

    private int getLeft() {
        return (int) (getNextStartId() - getCurrentId());
    }

    private boolean isEnough() {
        return !isExhausted() && (getLeft() / (double) getCurrentStep() > THRESHOLD);
    }

    public boolean isExhausted() {
        return !(getInitStatus() && getLeft() > 0);
    }

    public Long take() {
        notifyQueueIfNotInitialized();
        try {
            if (isExhausted()) {
                return null;
            }
            try {
                valueLock.readLock().lock();
                if (isExhausted()) {
                    return null;
                }
                return doTake();
            } finally {
                valueLock.readLock().unlock();
            }
        } finally {
            notifyQueueIfNotEnough();
        }
    }

    private void notifyQueueIfNotInitialized() {
        if (getInitStatus()) {
            return;
        }
        queue.notifyInitSegment(this);
    }

    private void notifyQueueIfNotEnough() {
        if (isEnough()) {
            return;
        }
        queue.notifyPrepareBackupSegment();
    }

    private Long doTake() {
        long id = value.getAndIncrement();
        if (id < getNextStartId()) {
            return id;
        }
        return null;
    }

    public int calcNextStep() {
        assertHasInitialized();
        long duration = System.currentTimeMillis() - getLastUpdateTime();
        int nextStep = getCurrentStep();
        if (duration < SEGMENT_DURATION_MS) {
            if (nextStep * 2 > MAX_STEP) {

            } else {
                nextStep = nextStep * 2;
            }
        } else if (duration < SEGMENT_DURATION_MS * 2) {

        } else {
            nextStep = nextStep / 2 >= getDbMinStep() ? nextStep / 2 : nextStep;
        }
        return nextStep;
    }

    public void updateCapacity(long nowStartId, int nextStep) {
        assertHasInitialized();
        try {
            valueLock.writeLock().lock();
            doUpdateCapacity(nowStartId, nextStep);
        } finally {
            valueLock.writeLock().unlock();
        }
    }

    private void doUpdateCapacity(long nextStartId, int nextStep) {
        setCurrentId(calcCurrentId(nextStartId, nextStep));
        setCurrentStep(nextStep);

        setNextStartId(nextStartId);
        refreshLastUpdateTime();
        incrementUpdateCapacityTimes();
    }

    private long calcCurrentId(long nextStartId, int currentStep) {
        return nextStartId - currentStep;
    }

    private void refreshLastUpdateTime() {
        setLastUpdateTime(System.currentTimeMillis());
    }

    private void incrementUpdateCapacityTimes() {
        updateCapacityTimes.incrementAndGet();
    }

    long getUpdateCapacityTimes() {
        return updateCapacityTimes.get();
    }

    //--------------------------------------------------------------------------------------------------//

    private void setDbMinStep(int dbMinStep) {
        this.dbMinStep = dbMinStep;
    }

    private void setDbStartId(long dbStartId) {
        this.dbStartId = dbStartId;
    }

    private void setCurrentId(long value) {
        this.value.set(value);
    }

    private void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    private void setNextStartId(long nextStartId) {
        this.nextStartId = nextStartId;
    }

    private void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    //--------------------------------------------------------------------------------------------------//

    private void assertHasInitialized() {
        if (!getInitStatus()) {
            throw HareException.of("segment has not initialized");
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }

    IdSequenceSegmentStatus getStatusInfo() {
        IdSequenceSegmentStatus status = new IdSequenceSegmentStatus();
        status.setInitStatus(getInitStatus());
        status.setLastUpdateTime(getLastUpdateTime());
        status.setDbStartId(getDbStartId());
        status.setDbMinStep(getDbMinStep());
        status.setNextStartId(getNextStartId());
        status.setCurrentStep(getCurrentStep());
        status.setCurrentId(getCurrentId());
        status.setLeftStep(getLeft());
        status.setIsEnough(isEnough());
        status.setIsExhausted(isExhausted());
        status.setUpdateTimes(getUpdateCapacityTimes());
        return status;
    }
}
