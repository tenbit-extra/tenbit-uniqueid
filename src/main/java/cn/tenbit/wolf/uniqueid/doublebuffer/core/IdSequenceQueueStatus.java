package cn.tenbit.wolf.uniqueid.doublebuffer.core;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author bangquan.qian
 * @Date 2019-05-23 13:31
 */
@Getter
@Setter
@NoArgsConstructor
public class IdSequenceQueueStatus {

    private Integer bizType;

    private Boolean initStatus;

    private Integer currentSegmentIndex;

    private Long updateTimes;

    private IdSequenceSegmentStatus segment0Status;

    private IdSequenceSegmentStatus segment1Status;
}
