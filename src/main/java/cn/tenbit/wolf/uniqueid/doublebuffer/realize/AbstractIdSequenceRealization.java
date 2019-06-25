package cn.tenbit.wolf.uniqueid.doublebuffer.realize;

import cn.tenbit.hare.core.lite.exception.HareException;
import cn.tenbit.wolf.uniqueid.doublebuffer.core.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author bangquan.qian
 * @Date 2019-05-23 02:46
 */

public abstract class AbstractIdSequenceRealization implements IdSequenceRealizable, IdSequenceSegmentRefreshable {

    private final Map<Integer, IdSequenceQueue> queueMap = new ConcurrentHashMap<>();

    private final Lock initLock = new ReentrantLock();

    private final AtomicBoolean initStatus = new AtomicBoolean(false);

    @Override
    public IdSequenceResult getOne(Integer type) {
        initQueueIfNecessary();

        IdSequenceQueue queue = getQueue(type);
        if (queue == null) {
            return buildResult(null, null, null);
        }

        IdSequenceSegment segment = null;
        Long id = null;
        while (true) {
            segment = queue.getCurrentSegment();
            id = segment.take();
            if (id != null && id > 0L) {
                break;
            }
            if (!queue.isReady()) {
                queue.switchCurrentSegmentIfNotReady();
            }
        }

        return buildResult(queue, segment, id);
    }

    private void initQueueIfNecessary() {
        if (initStatus.get()) {
            return;
        }

        try {
            initLock.lock();

            if (initStatus.get()) {
                return;
            }

            syncQueueMap(queueMap);
            initStatus.compareAndSet(false, true);
        } catch (Exception e) {
            throw HareException.of(e);
        } finally {
            initLock.unlock();
        }
    }

    private IdSequenceResult buildResult(IdSequenceQueue queue, IdSequenceSegment segment, Long id) {
        int segmentIndex = queue == null ? IdSequenceQueue.DEFAULT_SEGMENT_INDEX : queue.getCurrentSegmentIndex();

        int status = id != null && id > 0L ? IdSequenceResultStatus.SUCCESS.getType()
                : IdSequenceResultStatus.FAILURE.getType();

        IdSequenceResult result = new IdSequenceResult();
        result.setUniqueId(id);
        result.setSegmentIndex(segmentIndex);
        result.setStatus(status);
        return result;
    }

    private IdSequenceQueue getQueue(Integer type) {
        return queueMap.get(type);
    }

    @Override
    public IdSequenceQueueStatus getStatus(Integer type) {
        IdSequenceQueue queue = getQueue(type);
        return queue == null ? null : queue.getStatusInfo();
    }

    @Override
    public void refreshSegment(IdSequenceSegment segment) {
        if (!segment.isExhausted()) {
            return;
        }

        int nextStep = segment.calcNextStep();
        IdSequenceEntity entity = customSyncRepo(segment.getQueue().getBizType(), nextStep);
        segment.updateCapacity(entity.getStart(), nextStep);
    }

    @Override
    public void initSegment(IdSequenceSegment segment) {
        if (segment.getInitStatus()) {
            return;
        }

        IdSequenceEntity entity = syncRepo(segment.getQueue().getBizType());
        segment.init(entity.getStart(), entity.getStep());
    }

    public abstract IdSequenceEntity customSyncRepo(Integer bizType, int nextStep);

    public abstract IdSequenceEntity syncRepo(Integer bizType);

    protected abstract void syncQueueMap(Map<Integer, IdSequenceQueue> queueMap) throws Exception;
}
