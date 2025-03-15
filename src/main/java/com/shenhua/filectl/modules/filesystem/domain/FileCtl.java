package com.shenhua.filectl.modules.filesystem.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shenhua.filectl.common.web.base.domain.BaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 文件信息存储
 * </p>
 *
 * @author hshi22
 * @since 2023-11-23
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_file")
public class FileCtl extends BaseDomain {

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件相对路径
     */
    private String filePath;

    /**
     * 文件类型：.png等
     */
    private String type;

    /**
     * 文件大小
     */
    private String size;

    /**
     * 文件所属模块，如有的文件是设备身上的。会在系统中定义code处理
     */
    private String moduleName;
}
