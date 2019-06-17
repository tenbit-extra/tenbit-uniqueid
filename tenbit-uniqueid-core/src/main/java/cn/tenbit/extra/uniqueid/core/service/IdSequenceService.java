package cn.tenbit.extra.uniqueid.core.service;

import cn.tenbit.extra.uniqueid.core.contain.IdSequenceBeanContainer;
import cn.tenbit.extra.uniqueid.core.generate.DefaultIdSequenceGenerator;
import cn.tenbit.extra.uniqueid.core.generate.IdSequenceGenerate;
import cn.tenbit.hare.lite.exception.HareException;

/**
 * @Author bangquan.qian
 * @Date 2019-05-16 15:16
 */
public class IdSequenceService {

    private IdSequenceGenerate defaultIdSequenceGenerator = IdSequenceBeanContainer.get(DefaultIdSequenceGenerator.class);

    public Long genOne(Integer genType, Integer bizType) {
        IdSequenceGenerate generate = getGenerator(genType);
        if (generate == null) {
            generate = defaultIdSequenceGenerator;
        }
        Long id = generate.get(bizType);
        if (id == null || id < 1L) {
            throw HareException.of("gen id failed");
        }
        return id;
    }

    protected IdSequenceGenerate getGenerator(Integer genType) {
        return null;
    }
}
