package com.shenhua.filectl.modules.filesystem.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传后的返回信息
 */
@Data
public class FileInfo implements Serializable {
    public static final long serialVersionUID = 1L;

    private String name;

    /**
     * 用于给前端关联
     */
    private String fileId;

    public FileInfo() {
    }

    public FileInfo(String name, String fileId) {
        this.name = name;
        this.fileId = fileId;
    }

}
