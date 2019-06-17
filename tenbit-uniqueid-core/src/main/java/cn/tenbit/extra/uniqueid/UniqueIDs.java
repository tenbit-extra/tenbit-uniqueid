package cn.tenbit.extra.uniqueid;

import cn.tenbit.extra.uniqueid.support.IdSequenceStaticSupport;

/**
 * @Author bangquan.qian
 * @Date 2019-05-16 15:24
 */
public class UniqueIDs {

    /**
     * 获取示例分布式ID
     */
    public static Long getDefault(Integer bizType) {
        return IdSequenceStaticSupport.genOneFromDefault(bizType);
    }
}
