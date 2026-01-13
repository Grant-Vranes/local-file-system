package com.shenhua.filectl.modules.filesystem.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 分片上传请求参数
 */
@Data
public class ChunkUploadRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 文件唯一标识(通常是文件MD5)
     */
    private String identifier;

    /**
     * 当前分片序号(从1开始)
     */
    private Integer chunkNumber;

    /**
     * 分片大小(字节)
     */
    private Long chunkSize;

    /**
     * 当前分片实际大小(字节)
     */
    private Long currentChunkSize;

    /**
     * 文件总大小(字节)
     */
    private Long totalSize;

    /**
     * 总分片数
     */
    private Integer totalChunks;

    /**
     * 原始文件名
     */
    private String filename;
}
