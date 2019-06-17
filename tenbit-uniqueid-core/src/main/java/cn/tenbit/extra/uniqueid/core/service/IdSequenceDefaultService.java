package cn.tenbit.extra.uniqueid.core.service;

import cn.tenbit.extra.uniqueid.core.contain.IdSequenceBeanContainer;
import cn.tenbit.extra.uniqueid.core.model.IdSequenceResult;
import cn.tenbit.extra.uniqueid.core.realization.IdSequenceDefaultRealization;
import cn.tenbit.hare.lite.exception.HareException;

/**
 * @Author bangquan.qian
 * @Date 2019-05-16 14:48
 */
public class IdSequenceDefaultService {

    private IdSequenceDefaultRealization realize = IdSequenceBeanContainer.get(IdSequenceDefaultRealization.class);

    public Long genOne(Integer bizType) {
        if (bizType == null) {
            throw HareException.of("bizType invalid");
        }
        IdSequenceResult result = realize.getOne(bizType);
        if (result == null || result.getUniqueId() == null) {
            throw HareException.of("result invalid");
        }
        return result.getUniqueId();
    }
}
