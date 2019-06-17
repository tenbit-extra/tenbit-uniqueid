package cn.tenbit.extra.uniqueid.model;

import cn.tenbit.extra.uniqueid.support.IdSequenceSegmentRefreshable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 双buffer实现
 *
 * @Author bangquan.qian
 * @Date 2019-05-18 17:56
 */
public class IdSequenceQueue {

    public static final int DEFAULT_SEGMENT_INDEX = -1;

    //--------------------------------------------------------------------------------------------------//

    private final IdSequenceSegment segment0 = new IdSequenceSegment(this);

    private final IdSequenceSegment segment1 = new IdSequenceSegment(this);

    private final AtomicReference<IdSequenceSegment> currentSegment = new AtomicReference<>(segment0);

    private final Lock switchSegmentLock = new ReentrantLock();

    private final AtomicBoolean initStatus = new AtomicBoolean(false);

    private final Lock initLock = new ReentrantLock();

    private IdSequenceSegmentRefreshable refreshable;

    private Integer bizType;

    public IdSequenceQueue(IdSequenceSegmentRefreshable refreshable, Integer bizType) {
        insideInit(refreshable, bizType);
    }

    private void insideInit(IdSequenceSegmentRefreshable refreshable, Integer bizType) {
        this.refreshable = refreshable;
        this.bizType = bizType;
    }

    public Integer getBizType() {
        return bizType;
    }

    private boolean getInitStatus() {
        return initStatus.get();
    }

    public void setHasInitialized() {
        if (getInitStatus()) {
            return;
        }
        try {
            initLock.lock();
            if (getInitStatus()) {
                return;
            }
            initStatus.compareAndSet(false, true);
        } finally {
            initLock.unlock();
        }
    }

    public void switchCurrentSegmentIfNotReady() {
        assertHasInitialized();
        if (isReady()) {
            return;
        }
        try {
            switchSegmentLock.lock();
            if (isReady()) {
                return;
            }
            this.currentSegment.compareAndSet(getCurrentSegment(), getBackupSegment());
        } finally {
            switchSegmentLock.unlock();
        }
    }

    public boolean isReady() {
        return getInitStatus() && !getCurrentSegment().isExhausted();
    }

    private IdSequenceSegment getSegment0() {
        return segment0;
    }

    private IdSequenceSegment getSegment1() {
        return segment1;
    }

    public IdSequenceSegment getCurrentSegment() {
        return currentSegment.get();
    }

    public IdSequenceSegment getBackupSegment() {
        return getCurrentSegment() == getSegment0() ? getSegment1() : getSegment0();
    }

    void notifyPrepareBackupSegment() {
        IdSequenceSegment segment = getBackupSegment();
        if (!segment.getInitStatus()) {
            notifyInitSegment(segment);
        }

        if (!segment.isExhausted()) {
            return;
        }
        if (!segment.getQueueLock().tryLock()) {
            return;
        }
        try {
            if (!segment.isExhausted()) {
                return;
            }
            refreshable.refreshSegment(getBackupSegment());
        } finally {
            segment.getQueueLock().unlock();
        }
    }

    void notifyInitSegment(IdSequenceSegment segment) {
        if (segment.getInitStatus()) {
            return;
        }

        try {
            segment.getQueueLock().lock();
            if (segment.getInitStatus()) {
                return;
            }
            refreshable.initSegment(segment);
        } finally {
            segment.getQueueLock().unlock();
        }
    }

    private long getUpdateTimes() {
        return getSegment0().getUpdateCapacityTimes() + getSegment1().getUpdateCapacityTimes();
    }

    public int getCurrentSegmentIndex() {
        IdSequenceSegment segment = getCurrentSegment();
        int segmentIndex = DEFAULT_SEGMENT_INDEX;
        if (segment == getSegment0()) {
            segmentIndex = 0;
        } else if (segment == getSegment1()) {
            segmentIndex = 1;
        }
        return segmentIndex;
    }

    private void assertHasInitialized() {
        if (!getInitStatus()) {
            throw new RuntimeException("queue has not initialized");
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public IdSequenceQueueStatus getStatusInfo() {
        IdSequenceQueueStatus status = new IdSequenceQueueStatus();
        status.setBizType(getBizType());
        status.setCurrentSegmentIndex(getCurrentSegmentIndex());
        status.setInitStatus(getInitStatus());
        status.setUpdateTimes(getUpdateTimes());
        status.setSegment0Status(getSegment0().getStatusInfo());
        status.setSegment1Status(getSegment1().getStatusInfo());
        return status;
    }
}
