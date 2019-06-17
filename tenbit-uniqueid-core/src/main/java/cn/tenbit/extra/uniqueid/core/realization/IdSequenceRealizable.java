package cn.tenbit.extra.uniqueid.core.realization;

import cn.tenbit.extra.uniqueid.core.model.IdSequenceQueueStatus;
import cn.tenbit.extra.uniqueid.core.model.IdSequenceResult;

/**
 * @Author bangquan.qian
 * @Date 2019-05-22 18:01
 */

public interface IdSequenceRealizable {

    IdSequenceResult getOne(Integer type);

    IdSequenceQueueStatus getStatus(Integer type);
}
