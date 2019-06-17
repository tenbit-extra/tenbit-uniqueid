package cn.tenbit.extra.uniqueid.core.support;

import cn.tenbit.extra.uniqueid.core.contain.IdSequenceBeanContainer;
import cn.tenbit.extra.uniqueid.core.generate.DefaultIdSequenceGenerator;
import cn.tenbit.extra.uniqueid.core.realization.IdSequenceDefaultRealization;
import cn.tenbit.extra.uniqueid.core.service.IdSequenceDefaultService;
import cn.tenbit.extra.uniqueid.core.service.IdSequenceService;

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
