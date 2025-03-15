package com.shenhua.filectl.common.web.base.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Base Tree 实体
 * */
@Data
public class TreeDomain<T> extends BaseDomain{

    /**
     * 父级编号
     * */
    @TableField("parent_id")
    private String parentId;

    /**
     * 子级集合
     * */
    @TableField(exist = false)
    private List<T> children = new ArrayList<>();

    /**
     * 排序值
     */
    @TableField("sort_order")
    private Integer sortOrder;

}
