package cn.tenbit.extra.uniqueid.core.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author bangquan.qian
 * @Date 2019-05-15 18:36
 */
@Getter
@Setter
@NoArgsConstructor
public class IdSequenceEntity implements Serializable {
    private static final long serialVersionUID = 8457935798068262216L;

    private Integer id;

    /**
     * 业务类型
     */
    private Integer type;

    /**
     * 下次开始的ID
     */
    private Long start;

    /**
     * 初始最小步长
     */
    private Integer step;

    /**
     * 数据库更新时间
     */
    private Date updateTime;
}
