package cn.tenbit.wolf.uniqueid.doublebuffer.core;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author bangquan.qian
 * @Date 2019-05-18 17:39
 */

public enum IdSequenceResultStatus {

    SUCCESS(0),
    FAILURE(-1),
    ;

    @Getter
    Integer type;

    IdSequenceResultStatus(Integer type) {
        this.type = type;
    }

    public static IdSequenceResultStatus getType(Integer type) {
        return type == null ? null : getType((int) type);
    }

    public static IdSequenceResultStatus getType(int type) {
        for (IdSequenceResultStatus enu : values()) {
            if (enu.type == type) {
                return enu;
            }
        }
        return null;
    }

    private static final Map<Integer, IdSequenceResultStatus> CACHE_MAP = new HashMap<>();

    static {
        for (IdSequenceResultStatus enu : values()) {
            CACHE_MAP.put(enu.type, enu);
        }
    }

    public static IdSequenceResultStatus quickGetType(Integer type) {
        return type == null ? null : CACHE_MAP.get(type);
    }
}
