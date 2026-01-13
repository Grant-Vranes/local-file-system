package com.shenhua.filectl.modules.filesystem.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 分片上传响应
 */
@Data
public class ChunkUploadResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 是否需要上传此分片
     */
    private Boolean needUpload;

    /**
     * 已上传的分片序号列表
     */
    private Set<Integer> uploadedChunks;

    /**
     * 是否已完成所有分片上传
     */
    private Boolean merged;

    /**
     * 合并后的文件信息(仅在merged=true时有值)
     */
    private FileInfo fileInfo;

    /**
     * 消息
     */
    private String message;

    public ChunkUploadResponse() {
    }

    public ChunkUploadResponse(Boolean needUpload, Set<Integer> uploadedChunks) {
        this.needUpload = needUpload;
        this.uploadedChunks = uploadedChunks;
        this.merged = false;
    }

    public ChunkUploadResponse(FileInfo fileInfo) {
        this.merged = true;
        this.fileInfo = fileInfo;
        this.needUpload = false;
    }
}
