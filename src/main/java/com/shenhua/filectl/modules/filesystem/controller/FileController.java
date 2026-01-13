package com.shenhua.filectl.modules.filesystem.controller;


import com.shenhua.filectl.common.aop.annotation.CheckSupportUploadFileType;
import com.shenhua.filectl.common.constants.FileSystemConstant;
import com.shenhua.filectl.common.constants.FileType;
import com.shenhua.filectl.common.web.base.controller.BaseController;
import com.shenhua.filectl.common.web.domain.Result;
import com.shenhua.filectl.modules.filesystem.request.ChunkUploadRequest;
import com.shenhua.filectl.modules.filesystem.response.ChunkUploadResponse;
import com.shenhua.filectl.modules.filesystem.response.FileInfo;
import com.shenhua.filectl.modules.filesystem.service.IFileService;
import com.shenhua.filectl.modules.filesystem.service.ILinkFileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * <p>
 * 文件列表 前端控制器
 * </p>
 *
 * @author hshi22
 * @since 2023-11-23
 */
@RestController
@RequestMapping("/file")
public class FileController extends BaseController {

    @Resource
    private IFileService fileService;
    @Resource
    private ILinkFileService linkFileService;

    /**
     * 上传文件，根据module类型，选择性上传到对应的子文件夹
     * 上传文件到指定module对应的文件夹
     * @param module
     * @param file
     * @return
     */
    @PostMapping("/upload-with-module")
    @CheckSupportUploadFileType(fileArgsIndex = 1,
            supportUploadFileType = {FileType.JPG,
                    FileType.JPEG,
                    FileType.PNG,
                    FileType.GIF,
                    FileType.BMP,
                    FileType.PDF,
                    FileType.WORD_97_2003,
                    FileType.WORD_2007,
                    FileType.POWERPOINT_97_2007,
                    FileType.POWERPOINT_2007,
                    FileType.EXCEL_97_2003,
                    FileType.EXCEL_2007},
            supportUploadFileSuffix = {FileType.JPG,
                    FileType.JPEG,
                    FileType.PNG,
                    FileType.GIF,
                    FileType.BMP,
                    FileType.PDF,
                    FileType.WORD_97_2003,
                    FileType.WORD_2007,
                    FileType.POWERPOINT_97_2007,
                    FileType.POWERPOINT_2007,
                    FileType.EXCEL_97_2003,
                    FileType.EXCEL_2007
            })
    public Result<FileInfo> uploadWithModule(@RequestParam(name = "module", defaultValue = "DEFAULT") FileSystemConstant.Module module,
                                             @RequestBody MultipartFile file){
        return success(fileService.uploadWithModule(module, file));
    }

    /**
     * 上传文件，上传到统一文件池
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @CheckSupportUploadFileType(supportUploadFileType = {FileType.JPG,
                    FileType.JPEG,
                    FileType.PNG,
                    FileType.GIF,
                    FileType.BMP,
                    FileType.PDF,
                    FileType.WORD_97_2003,
                    FileType.WORD_2007,
                    FileType.POWERPOINT_97_2007,
                    FileType.POWERPOINT_2007,
                    FileType.EXCEL_97_2003,
                    FileType.EXCEL_2007},
            supportUploadFileSuffix = {FileType.JPG,
                    FileType.JPEG,
                    FileType.PNG,
                    FileType.GIF,
                    FileType.BMP,
                    FileType.PDF,
                    FileType.WORD_97_2003,
                    FileType.WORD_2007,
                    FileType.POWERPOINT_97_2007,
                    FileType.POWERPOINT_2007,
                    FileType.EXCEL_97_2003,
                    FileType.EXCEL_2007
            })
    public Result<FileInfo> upload(@RequestBody MultipartFile file){
        return success(fileService.upload(file));
    }

    /**
     * 上传文件到指定module对应的文件夹
     * 通过URL的方式
     * @param module
     * @param fileUrl 网络地址
     * @return
     */
    @PostMapping("/upload-with-module-by-url")
    public Result<FileInfo> uploadWithModuleByURL(@RequestParam(name = "module", defaultValue = "DEFAULT") FileSystemConstant.Module module,
                                  @RequestParam("fileUrl") String fileUrl,
                                  @RequestParam("fileName") String fileName){
        return success(fileService.uploadWithModuleByURL(module, fileUrl, fileName));
    }

    /**
     * 根据文件ID下载文件
     * @param fileId 文件id
     * @return
     */
    @GetMapping("/download/{fileId}")
    public Object downloadHandler(HttpServletResponse response, @PathVariable("fileId") String fileId) {
        return fileService.downloadHandler(response, fileId);
    }

    /**
     * 根据文件Id获取FileInfo信息
     * @param fileId
     * @return
     */
    @GetMapping("/file-info/{fileId}")
    public Result getFileInfoByFileId(@PathVariable("fileId") String fileId) {
        return success(fileService.getFileInfoByFileId(fileId));
    }

    /**
     * 根据模块和linkId获得对应文件
     * 即根据某实体的id获取在某一个module上传的文件info集合
     * @param module
     * @param linkId
     * @return
     */
    @GetMapping("/file-info-by-link-id")
    public Result getFileInfoByLinkId(@RequestParam(name = "module") FileSystemConstant.Module module,
                                       @RequestParam("linkId") String linkId) {
        return success(linkFileService.getFileInfoByLinkId(module, linkId));
    }

    /**
     * 检查分片上传状态
     * @param identifier 文件唯一标识(MD5)
     * @return 已上传的分片序号集合
     */
    @GetMapping("/chunk/check")
    public Result<Set<Integer>> checkChunkStatus(@RequestParam("identifier") String identifier) {
        return success(fileService.checkChunkStatus(identifier));
    }

    /**
     * 分片上传
     * @param module 模块
     * @param identifier 文件唯一标识(MD5)
     * @param chunkNumber 当前分片序号(从1开始)
     * @param chunkSize 分片大小
     * @param currentChunkSize 当前分片实际大小
     * @param totalSize 文件总大小
     * @param totalChunks 总分片数
     * @param filename 原始文件名
     * @param chunk 分片文件数据
     * @return 上传响应
     */
    @PostMapping("/chunk/upload")
    public Result<ChunkUploadResponse> uploadChunk(
            @RequestParam(name = "module", defaultValue = "DEFAULT") FileSystemConstant.Module module,
            @RequestParam("identifier") String identifier,
            @RequestParam("chunkNumber") Integer chunkNumber,
            @RequestParam("chunkSize") Long chunkSize,
            @RequestParam("currentChunkSize") Long currentChunkSize,
            @RequestParam("totalSize") Long totalSize,
            @RequestParam("totalChunks") Integer totalChunks,
            @RequestParam("filename") String filename,
            @RequestParam("chunk") MultipartFile chunk) {
        
        ChunkUploadRequest request = new ChunkUploadRequest();
        request.setIdentifier(identifier);
        request.setChunkNumber(chunkNumber);
        request.setChunkSize(chunkSize);
        request.setCurrentChunkSize(currentChunkSize);
        request.setTotalSize(totalSize);
        request.setTotalChunks(totalChunks);
        request.setFilename(filename);
        
        return success(fileService.uploadChunk(module, request, chunk));
    }

    /**
     * 手动触发合并分片
     * @param module 模块
     * @param identifier 文件唯一标识
     * @param totalChunks 总分片数
     * @param totalSize 文件总大小
     * @param filename 原始文件名
     * @return 文件信息
     */
    @PostMapping("/chunk/merge")
    public Result<FileInfo> mergeChunks(
            @RequestParam(name = "module", defaultValue = "DEFAULT") FileSystemConstant.Module module,
            @RequestParam("identifier") String identifier,
            @RequestParam("totalChunks") Integer totalChunks,
            @RequestParam("totalSize") Long totalSize,
            @RequestParam("filename") String filename) {
        
        ChunkUploadRequest request = new ChunkUploadRequest();
        request.setIdentifier(identifier);
        request.setTotalChunks(totalChunks);
        request.setTotalSize(totalSize);
        request.setFilename(filename);
        
        return success(fileService.mergeChunks(module, request));
    }
}
