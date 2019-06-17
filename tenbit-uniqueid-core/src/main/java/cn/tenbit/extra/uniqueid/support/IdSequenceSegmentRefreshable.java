package cn.tenbit.extra.uniqueid.support;

import cn.tenbit.extra.uniqueid.model.IdSequenceSegment;

/**
 * @Author bangquan.qian
 * @Date 2019-05-23 00:43
 */

public interface IdSequenceSegmentRefreshable {

    void refreshSegment(IdSequenceSegment segment);

    void initSegment(IdSequenceSegment segment);
}
