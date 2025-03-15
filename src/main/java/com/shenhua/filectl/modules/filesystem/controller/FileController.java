package com.shenhua.filectl.modules.filesystem.controller;


import com.shenhua.filectl.common.aop.annotation.CheckSupportUploadFileType;
import com.shenhua.filectl.common.constants.FileSystemConstant;
import com.shenhua.filectl.common.constants.FileType;
import com.shenhua.filectl.common.web.base.controller.BaseController;
import com.shenhua.filectl.common.web.domain.Result;
import com.shenhua.filectl.modules.filesystem.response.FileInfo;
import com.shenhua.filectl.modules.filesystem.service.IFileService;
import com.shenhua.filectl.modules.filesystem.service.ILinkFileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

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
    @PostMapping("/uploadWithModule")
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
     * 根据文件ID下载文件
     * @param fileId 文件id
     * @return
     */
    @GetMapping("/download/{fileId}")
    public Object downloadHandler(HttpServletResponse response, @PathVariable("fileId") String fileId) {
        return fileService.downloadHandler(response, fileId);
    }
}
