package cn.tenbit.extra.uniqueid.support;

import cn.tenbit.extra.uniqueid.contain.IdSequenceBeanContainer;
import cn.tenbit.extra.uniqueid.generate.DefaultIdSequenceGenerator;
import cn.tenbit.extra.uniqueid.realization.IdSequenceDefaultRealization;
import cn.tenbit.extra.uniqueid.service.IdSequenceDefaultService;
import cn.tenbit.extra.uniqueid.service.IdSequenceService;

/**
 * @Author bangquan.qian
 * @Date 2019-06-17 14:59
 */
public class IdSequenceBeanContainerSupport {

    static {
        IdSequenceBeanContainer.add(IdSequenceService.class);
        IdSequenceBeanContainer.add(DefaultIdSequenceGenerator.class);
        IdSequenceBeanContainer.add(IdSequenceDefaultService.class);
        IdSequenceBeanContainer.add(IdSequenceDefaultRealization.class);
    }
}
