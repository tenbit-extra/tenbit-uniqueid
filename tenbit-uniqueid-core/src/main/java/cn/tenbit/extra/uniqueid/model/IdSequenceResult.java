package cn.tenbit.extra.uniqueid.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author bangquan.qian
 * @Date 2019-05-18 17:39
 */
@Getter
@Setter
@NoArgsConstructor
public class IdSequenceResult {

    private Long uniqueId;

    private Integer status = IdSequenceResultStatus.FAILURE.getType();

    private Integer segmentIndex;
}
