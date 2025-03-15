package com.shenhua.filectl.modules.filesystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shenhua.filectl.common.configure.SupportUploadFileTypeConfig;
import com.shenhua.filectl.common.constants.FileSystemConstant;
import com.shenhua.filectl.common.utils.DatePlusUtil;
import com.shenhua.filectl.common.utils.FileUtil;
import com.shenhua.filectl.common.utils.HttpsClientUtil;
import com.shenhua.filectl.common.utils.UUIDUtils;
import com.shenhua.filectl.common.web.domain.ResultCode;
import com.shenhua.filectl.common.web.exception.base.BusinessException;
import com.shenhua.filectl.modules.filesystem.domain.FileCtl;
import com.shenhua.filectl.modules.filesystem.repository.FileMapper;
import com.shenhua.filectl.modules.filesystem.response.FileInfo;
import com.shenhua.filectl.modules.filesystem.service.IFileService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

/**
 * <p>
 * ???? 服务实现类
 * </p>
 *
 * @author hshi22
 * @since 2023-11-23
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileCtl> implements IFileService {

    private static final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

    // @Resource
    // private SecurityUserTokenService customUserDetailsTokenService;

    @Resource
    private SupportUploadFileTypeConfig supportUploadFileTypeConfig;

    @Value("${file-system.prefix}")
    private String filePrefix;

    /**
     * 初始化文件存储信息, 使用默认module
     * @param file
     * @param filePath 文件的相对路径，相对于file-system.prefix
     * @return
     */
    @Override
    public FileCtl initFileCtlInfo(MultipartFile file, String filePath) {
        return initFileCtlInfo(file, filePath, FileSystemConstant.Module.DEFAULT);
    }

    /**
     * 初始化文件存储信息
     * @param file
     * @param filePath 文件的相对路径，相对于file-system.prefix
     * @param module 模块名，背后通过FileSystemConstant.Module对应着具体的文件夹
     * @return
     */
    @Override
    public FileCtl initFileCtlInfo(MultipartFile file, String filePath, FileSystemConstant.Module module) {
        FileCtl fileCtl = new FileCtl();

        String fileName = file.getOriginalFilename();
        String fileSuffix = FileUtil.getFileSuffix(fileName);

        fileCtl.setFileName(fileName);
        fileCtl.setType(fileSuffix);
        fileCtl.setSize(FileUtil.getFileSizeDesc(file.getSize()));
        fileCtl.setFilePath(filePath);
        fileCtl.setModuleName(module.getModule());

        return fileCtl;
    }

    /**
     * 上传文件，返回对应的文件id和文件名
     * 存入默认文件夹
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public FileInfo upload(MultipartFile file) {
        // 为了防止同文件夹同名文件出现，需要对传入的文件名称做唯一处理
        String fileName = file.getOriginalFilename();
        String fileSuffix = FileUtil.getFileSuffix(fileName);
        String fileNameWithoutExtension = FileUtil.getFileNameWithoutExtension(fileName);
        fileName = FileUtil.concatenate(fileNameWithoutExtension, FileUtil.STANDARD_SPLICE, UUIDUtils.uuid(), fileSuffix);

        String relativePath = FileUtil.concatenateWithSlash(FileSystemConstant.FILE_DIR, DatePlusUtil.getCurrentDay(), fileName);
        FileUtil.transferToAuto(filePrefix, relativePath, file);
        FileCtl fileCtl = initFileCtlInfo(file, relativePath);
        save(fileCtl);

        // return new FileInfo(file.getOriginalFilename(), fileCtl.getId(), systemConfigService.generateDirectLink(fileCtl.getId()));
        return new FileInfo(file.getOriginalFilename(), fileCtl.getId());
    }

    /**
     * 上传文件到指定module对应的文件夹
     * @param module
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public FileInfo uploadWithModule(FileSystemConstant.Module module, MultipartFile file) {
        String folderName = module.getFolderName();

        // 为了防止同文件夹同名文件出现，需要对传入的文件名称做唯一处理
        String fileName = file.getOriginalFilename();
        String fileSuffix = FileUtil.getFileSuffix(fileName);
        String fileNameWithoutExtension = FileUtil.getFileNameWithoutExtension(fileName);
        fileName = FileUtil.concatenate(fileNameWithoutExtension, FileUtil.STANDARD_SPLICE, UUIDUtils.uuid(), fileSuffix);

        String relativePath = FileUtil.concatenateWithSlash(folderName, fileName);
        FileUtil.transferToAuto(filePrefix, relativePath, file);
        FileCtl fileCtl = initFileCtlInfo(file, relativePath, module);
        save(fileCtl);

        // return new FileInfo(file.getOriginalFilename(), fileCtl.getId(), systemConfigService.generateDirectLink(fileCtl.getId()));
        return new FileInfo(file.getOriginalFilename(), fileCtl.getId());
    }

    /**
     * 上传文件到指定module对应的文件夹
     * 通过URL的方式
     * @param module
     * @param fileUrl
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo uploadWithModuleByURL(FileSystemConstant.Module module, String fileUrl, String fileName) {
        String folderName = module.getFolderName();

        // 为了防止同文件夹同名文件出现，需要对传入的文件名称做唯一处理
        String fileSuffix = FileUtil.getFileSuffix(fileName);
        String fileNameWithoutExtension = FileUtil.getFileNameWithoutExtension(fileName);
        String trueFileName = FileUtil.concatenate(fileNameWithoutExtension, FileUtil.STANDARD_SPLICE, UUIDUtils.uuid(), fileSuffix);

        String relativePath = FileUtil.concatenateWithSlash(folderName, trueFileName);
        String absolutePath = filePrefix + relativePath;
        FileUtil.buildFolderIfNotExist(absolutePath);
        LOG.info("uploadWithModuleByURL absolutePath => {}", absolutePath);

        try {
            String fileBase64 = HttpsClientUtil.getBase64FromUrl(fileUrl);
            // 解码Base64字符串
            byte[] decodedBytes = Base64.getDecoder().decode(fileBase64);
            // 将解码后的数据写入文件
            try (FileOutputStream fos = new FileOutputStream(absolutePath)) {
                fos.write(decodedBytes);
            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
            LOG.error("uploadWithModuleByURL error => {}", e);
            e.printStackTrace();
        }

        FileCtl fileCtl = new FileCtl();
        fileCtl.setFileName(fileName);
        fileCtl.setType(fileSuffix);
        fileCtl.setSize("");
        fileCtl.setFilePath(relativePath);
        fileCtl.setModuleName(module.getModule());
        save(fileCtl);

        return new FileInfo(fileName, fileCtl.getId());
    }

    /**
     * 根据文件ID下载文件
     * @param response
     * @param fileId
     * @return
     */
    @Override
    public Object downloadHandler(HttpServletResponse response, String fileId) {
        if (StringUtils.isBlank(fileId)) {
            throw new BusinessException(ResultCode.MISS_PARAMETER);
        }
        // 根据文件ID获得文件记录
        FileCtl fileCtl = getById(fileId);
        if (Objects.isNull(fileCtl)) {
            throw new BusinessException(ResultCode.FILE_NOT_FOUND);
        }
        try {
            // 将文件写入输入流
            FileInputStream fileInputStream = new FileInputStream(filePrefix + fileCtl.getFilePath());
            return FileUtil.downloadFileForInline(fileInputStream, fileCtl.getFileName());
        } catch(FileNotFoundException e){
            LOG.error("FILE_NOT_FOUND => {}", e.toString());
            throw new BusinessException(ResultCode.FILE_NOT_FOUND);
        } catch (IOException e) {
            LOG.error("FILE_TRANSFER_ERROR => {}", e.toString());
            throw new BusinessException(ResultCode.FILE_TRANSFER_ERROR);
        }
    }

    /**
     * 根据文件ID下载文件
     * @param response
     * @param fileId 文件id
     * @param authorization token
     * @return
     */
    @Override
    public Object downloadHandlerAuth(HttpServletResponse response, String fileId, String authorization) {
        // 做授权验证
        try {
            // SecurityUser secureUser = customUserDetailsTokenService.verifyTokenNoRedis(authorization);
        } catch (Exception e){
            throw new BusinessException();
        }

        // 为空验证
        if (StringUtils.isBlank(fileId)) {
            throw new BusinessException(ResultCode.MISS_PARAMETER);
        }
        // 根据文件ID获得文件记录
        FileCtl fileCtl = getById(fileId);
        if (Objects.isNull(fileCtl)) {
            throw new BusinessException(ResultCode.MISS_PARAMETER);
        }
        try {
            // 将文件写入输入流
            FileInputStream fileInputStream = new FileInputStream(filePrefix + fileCtl.getFilePath());
            return FileUtil.downloadFileForInline(fileInputStream, fileCtl.getFileName());
        } catch(FileNotFoundException e){
            LOG.error("FILE_NOT_FOUND => {}", e.toString());
            throw new BusinessException(ResultCode.FILE_NOT_FOUND);
        } catch (IOException e) {
            LOG.error("FILE_TRANSFER_ERROR => {}", e.toString());
            throw new BusinessException(ResultCode.FILE_TRANSFER_ERROR);
        }
    }

    /**
     * 通过fileId获得FileInfo
     * @param fileId
     * @return
     */
    @Override
    public FileInfo getFileInfoByFileId(String fileId) {
        if (StringUtils.isBlank(fileId)) {
            return null;
        }
        FileCtl fileCtl = getById(fileId);
        // return new FileInfo(fileCtl.getFileName(), fileCtl.getId(), systemConfigService.generateDirectLink(fileCtl.getId()));
        return new FileInfo(fileCtl.getFileName(), fileCtl.getId());
    }
}
