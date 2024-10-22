package cn.tenbit.wolf.uniqueid.doublebuffer.realize;

import cn.tenbit.wolf.uniqueid.doublebuffer.core.IdSequenceEntity;
import cn.tenbit.wolf.uniqueid.doublebuffer.core.IdSequenceQueue;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author bangquan.qian
 * @Date 2019-05-18 17:38
 */
public class IdSequenceDefaultRealization extends AbstractIdSequenceRealization {

    private static final int MIN_STEP = 1;

    private static final int BIZ_TYPE = 0;

    private AtomicLong startId = new AtomicLong(1);

    @Override
    public synchronized IdSequenceEntity customSyncRepo(Integer bizType, int nextStep) {
        long nextStartId = startId.addAndGet(nextStep);
        IdSequenceEntity entity = new IdSequenceEntity();
        entity.setStart(nextStartId);
        entity.setStep(nextStep);
        return entity;
    }

    @Override
    public IdSequenceEntity syncRepo(Integer bizType) {
        return customSyncRepo(bizType, MIN_STEP);
    }

    @Override
    protected void syncQueueMap(Map<Integer, IdSequenceQueue> queueMap) throws Exception {
        IdSequenceQueue queue = new IdSequenceQueue(this, BIZ_TYPE);
        queue.setHasInitialized();

        queueMap.put(BIZ_TYPE, queue);
    }
}
