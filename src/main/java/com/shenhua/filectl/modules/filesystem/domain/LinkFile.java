package com.shenhua.filectl.modules.filesystem.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 文件与各个模块的关联
 * </p>
 *
 * @author hshi22
 * @since 2023-11-24
 */
@Data
@TableName("tr_link_file")
public class LinkFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 模块名，用于区分是和哪个表的关联
     */
    private String moduleName;

    /**
     * 关联表的ID
     */
    private String linkId;

    /**
     * 文件ID
     */
    private String fileId;

    public LinkFile() {
    }

    public LinkFile(String moduleName, String linkId, String fileId) {
        this.moduleName = moduleName;
        this.linkId = linkId;
        this.fileId = fileId;
    }
}
