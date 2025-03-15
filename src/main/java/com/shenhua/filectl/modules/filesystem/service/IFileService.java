package com.shenhua.filectl.modules.filesystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shenhua.filectl.common.constants.FileSystemConstant;
import com.shenhua.filectl.modules.filesystem.domain.FileCtl;
import com.shenhua.filectl.modules.filesystem.response.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 文件系统 服务类
 * </p>
 *
 * @author hshi22
 * @since 2023-11-23
 */
public interface IFileService extends IService<FileCtl> {

    FileCtl initFileCtlInfo(MultipartFile file, String filePath);
    FileCtl initFileCtlInfo(MultipartFile file, String filePath, FileSystemConstant.Module module);

    FileInfo upload(MultipartFile file);

    FileInfo uploadWithModule(FileSystemConstant.Module module, MultipartFile file);

    Object downloadHandler(HttpServletResponse response, String fileId);

    Object downloadHandlerAuth(HttpServletResponse response, String fileId, String authorization);

    FileInfo getFileInfoByFileId(String fileId);

    FileInfo uploadWithModuleByURL(FileSystemConstant.Module module, String fileUrl, String fileName);
}
