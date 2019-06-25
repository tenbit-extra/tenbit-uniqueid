package cn.tenbit.wolf.uniqueid.doublebuffer.realize;

import cn.tenbit.wolf.uniqueid.doublebuffer.core.IdSequenceQueueStatus;
import cn.tenbit.wolf.uniqueid.doublebuffer.core.IdSequenceResult;

/**
 * @Author bangquan.qian
 * @Date 2019-05-22 18:01
 */

public interface IdSequenceRealizable {

    IdSequenceResult getOne(Integer type);

    IdSequenceQueueStatus getStatus(Integer type);
}
