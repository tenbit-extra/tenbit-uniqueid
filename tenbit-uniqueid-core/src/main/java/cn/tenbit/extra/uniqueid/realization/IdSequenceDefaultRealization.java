package cn.tenbit.extra.uniqueid.realization;

import cn.tenbit.extra.uniqueid.constant.IdSequenceBizConsts;
import cn.tenbit.extra.uniqueid.entity.IdSequenceEntity;
import cn.tenbit.extra.uniqueid.model.IdSequenceQueue;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author bangquan.qian
 * @Date 2019-05-18 17:38
 */
public class IdSequenceDefaultRealization extends AbstractIdSequenceRealization {

    private static final int MIN_STEP = 1;

    private static final int BIZ_TYPE = IdSequenceBizConsts.EXAMPLE;

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
    public synchronized IdSequenceEntity syncRepo(Integer bizType) {
        return customSyncRepo(bizType, MIN_STEP);
    }

    @Override
    protected synchronized void syncQueueMap(Map<Integer, IdSequenceQueue> queueMap) throws Exception {
        IdSequenceQueue queue = new IdSequenceQueue(this, BIZ_TYPE);
        queue.setHasInitialized();

        queueMap.put(BIZ_TYPE, queue);
    }
}
