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

    /**
     * 初始化文件存储信息, 使用默认module
     * @param file
     * @param filePath 文件的相对路径，相对于file-system.prefix
     * @return
     */
    FileCtl initFileCtlInfo(MultipartFile file, String filePath);

    /**
     * 初始化文件存储信息
     * @param file
     * @param filePath 文件的相对路径，相对于file-system.prefix
     * @param module 模块名，背后通过FileSystemConstant.Module对应着具体的文件夹
     * @return
     */
    FileCtl initFileCtlInfo(MultipartFile file, String filePath, FileSystemConstant.Module module);

    /**
     * 上传文件，返回对应的文件id和文件名
     * 存入默认文件夹
     * @param file
     * @return
     */
    FileInfo upload(MultipartFile file);

    /**
     * 上传文件到指定module对应的文件夹
     * @param module 对应着存储文件夹
     * @param file
     * @return
     */
    FileInfo uploadWithModule(FileSystemConstant.Module module, MultipartFile file);

    /**
     * 根据文件ID下载文件
     * @param response
     * @param fileId
     * @return
     */
    Object downloadHandler(HttpServletResponse response, String fileId);

    /**
     * 通过fileId获得FileInfo
     * @param fileId
     * @return
     */
    FileInfo getFileInfoByFileId(String fileId);

    /**
     * 上传文件到指定module对应的文件夹
     * 通过URL的方式
     * @param module
     * @param fileUrl 网络地址
     * @return
     */
    FileInfo uploadWithModuleByURL(FileSystemConstant.Module module, String fileUrl, String fileName);
}
