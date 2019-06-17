package cn.tenbit.extra.uniqueid.support;

import cn.tenbit.extra.uniqueid.constant.IdSequenceGenConsts;
import cn.tenbit.extra.uniqueid.contain.IdSequenceBeanContainer;
import cn.tenbit.extra.uniqueid.service.IdSequenceService;
import lombok.Getter;

/**
 * @Author bangquan.qian
 * @Date 2019-05-15 18:43
 */
public class IdSequenceStaticSupport {

    private static class Instance {
        private static final IdSequenceStaticSupport INSTANCE = new IdSequenceStaticSupport();
    }

    @Getter
    private IdSequenceService service = IdSequenceBeanContainer.get(IdSequenceService.class);

    private static IdSequenceStaticSupport getInstance() {
        return Instance.INSTANCE;
    }

    public static Long genOneFromDefault(Integer bizType) {
        return getInstance().getService().genOne(IdSequenceGenConsts.DEFAULT, bizType);
    }
}
