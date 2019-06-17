package cn.tenbit.extra.uniqueid.core.contain;

import cn.tenbit.extra.uniqueid.core.support.IdSequenceBeanContainerSupport;
import cn.tenbit.hare.lite.constant.HareConsts;
import cn.tenbit.hare.lite.exception.HareException;
import cn.tenbit.hare.lite.util.HareAssertUtils;
import cn.tenbit.hare.lite.util.HareClassUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author bangquan.qian
 * @Date 2019-06-17 12:07
 */
public class IdSequenceBeanContainer {

    private static final Map<String, Object> MAP = new ConcurrentHashMap<>();

    static {
        HareClassUtils.loadClass(IdSequenceBeanContainerSupport.class);
    }

    public static void add(Class clz) {
        HareAssertUtils.isTrue(get(clz) == null, "duplicate bean");
        try {
            MAP.put(generateBeanName(clz), clz.newInstance());
        } catch (Exception e) {
            throw HareException.of(e);
        }
    }

    @SuppressWarnings(HareConsts.SUPPRESS_WARNING_UNCHECKED)
    public static <T> T get(Class clz) {
        return (T) MAP.get(generateBeanName(clz));
    }

    public static void remove(Class clz) {
        MAP.remove(generateBeanName(clz));
    }

    public static String generateBeanName(Class clz) {
        return clz.getCanonicalName();
    }

    private IdSequenceBeanContainer() {
    }
}
