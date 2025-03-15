package com.shenhua.filectl.common.web.base.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base 实体
 * */
@Data
public class BaseDomain implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     * */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 创建人
     * */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     * */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 创建人姓名
     */
    @TableField(value = "create_name", fill = FieldFill.INSERT)
    private String createName;

    /**
     * 修改人
     * */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 修改人姓名
     */
    @TableField(value = "update_name", fill = FieldFill.INSERT)
    private String updateName;

    /**
     * 修改时间
     * */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除
     * */
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;

}
