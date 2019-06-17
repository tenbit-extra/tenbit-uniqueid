package cn.tenbit.extra.uniqueid.generate;

import cn.tenbit.extra.uniqueid.contain.IdSequenceBeanContainer;
import cn.tenbit.extra.uniqueid.service.IdSequenceDefaultService;

/**
 * @Author bangquan.qian
 * @Date 2019-05-16 16:01
 */
public class DefaultIdSequenceGenerator implements IdSequenceGenerate {

    private IdSequenceDefaultService service = IdSequenceBeanContainer.get(IdSequenceDefaultService.class);

    @Override
    public Long get(Integer bizType) {
        return service.genOne(bizType);
    }
}
