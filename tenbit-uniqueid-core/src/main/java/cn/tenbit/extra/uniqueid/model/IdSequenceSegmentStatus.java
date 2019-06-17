package cn.tenbit.extra.uniqueid.model;

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
public class IdSequenceSegmentStatus {

    private Integer dbMinStep;

    private Long dbStartId;

    private Integer currentStep;

    private Long nextStartId;

    private Boolean initStatus;

    private Long lastUpdateTime;

    private Long currentId;

    private Integer leftStep;

    private Boolean isEnough;

    private Boolean isExhausted;

    private Long updateTimes;
}
