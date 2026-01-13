package com.shenhua.filectl.modules.filesystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shenhua.filectl.common.constants.FileSystemConstant;
import com.shenhua.filectl.common.utils.DatePlusUtil;
import com.shenhua.filectl.common.utils.FileUtil;
import com.shenhua.filectl.common.utils.HttpsClientUtil;
import com.shenhua.filectl.common.utils.UUIDUtils;
import com.shenhua.filectl.common.web.domain.ResultCode;
import com.shenhua.filectl.common.web.exception.base.BusinessException;
import com.shenhua.filectl.modules.filesystem.domain.FileCtl;
import com.shenhua.filectl.modules.filesystem.repository.FileMapper;
import com.shenhua.filectl.modules.filesystem.request.ChunkUploadRequest;
import com.shenhua.filectl.modules.filesystem.response.ChunkUploadResponse;
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
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 文件信息 服务实现类
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
        
        // 优化：对于大文件使用流式传输
        if (file.getSize() > 10 * 1024 * 1024) { // 超过10MB使用流式处理
            LOG.info("检测到大文件上传({}MB)，使用流式传输", file.getSize() / (1024 * 1024));
            transferLargeFile(filePrefix, relativePath, file);
        } else {
            FileUtil.transferToAuto(filePrefix, relativePath, file);
        }
        
        FileCtl fileCtl = initFileCtlInfo(file, relativePath, module);
        save(fileCtl);

        // return new FileInfo(file.getOriginalFilename(), fileCtl.getId(), systemConfigService.generateDirectLink(fileCtl.getId()));
        return new FileInfo(file.getOriginalFilename(), fileCtl.getId());
    }

    /**
     * 大文件流式传输
     */
    private void transferLargeFile(String prefix, String relativePath, MultipartFile file) {
        try {
            String absolutePath = prefix + relativePath;
            File targetFile = new File(absolutePath);
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            
            try (InputStream is = file.getInputStream();
                 FileOutputStream fos = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                fos.flush();
            }
        } catch (IOException e) {
            LOG.error("大文件传输失败 =>{}", e.toString());
            throw new BusinessException(ResultCode.FILE_TRANSFER_ERROR);
        }
    }

    /**
     * 上传文件到指定module对应的文件夹
     * 通过URL的方式
     * @param module
     * @param fileUrl 网络地址
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

    /**
     * 检查分片上传状态
     * @param identifier 文件唯一标识
     * @return 已上传的分片序号集合
     */
    @Override
    public Set<Integer> checkChunkStatus(String identifier) {
        String chunkFolderPath = getChunkFolderPath(identifier);
        File chunkFolder = new File(chunkFolderPath);
        
        if (!chunkFolder.exists() || !chunkFolder.isDirectory()) {
            return new HashSet<>();
        }
        
        File[] chunkFiles = chunkFolder.listFiles();
        if (chunkFiles == null || chunkFiles.length == 0) {
            return new HashSet<>();
        }
        
        return Arrays.stream(chunkFiles)
                .map(File::getName)
                .map(FileUtil::resolveChunkFileNumber)
                .collect(Collectors.toSet());
    }

    /**
     * 上传单个分片
     * @param module 模块
     * @param request 分片请求参数
     * @param chunk 分片文件数据
     * @return 上传响应
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ChunkUploadResponse uploadChunk(FileSystemConstant.Module module, ChunkUploadRequest request, MultipartFile chunk) {
        try {
            // 验证参数
            validateChunkRequest(request, chunk);
            
            // 检查该分片是否已上传
            Set<Integer> uploadedChunks = checkChunkStatus(request.getIdentifier());
            if (uploadedChunks.contains(request.getChunkNumber())) {
                LOG.info("分片已存在，跳过上传: identifier={}, chunkNumber={}", 
                    request.getIdentifier(), request.getChunkNumber());
                ChunkUploadResponse response = new ChunkUploadResponse(false, uploadedChunks);
                response.setMessage("分片已存在");
                return response;
            }
            
            // 保存分片文件
            String chunkFilePath = getChunkFilePath(request.getIdentifier(), request.getChunkNumber());
            File chunkFile = new File(chunkFilePath);
            if (!chunkFile.getParentFile().exists()) {
                chunkFile.getParentFile().mkdirs();
            }
            
            chunk.transferTo(chunkFile);
            uploadedChunks.add(request.getChunkNumber());
            
            LOG.info("分片上传成功: identifier={}, chunkNumber={}/{}", 
                request.getIdentifier(), request.getChunkNumber(), request.getTotalChunks());
            
            // 检查是否所有分片都已上传完成
            if (uploadedChunks.size() == request.getTotalChunks()) {
                LOG.info("所有分片上传完成，开始合并: identifier={}", request.getIdentifier());
                FileInfo fileInfo = mergeChunks(module, request);
                ChunkUploadResponse response = new ChunkUploadResponse(fileInfo);
                response.setMessage("文件上传并合并成功");
                return response;
            }
            
            ChunkUploadResponse response = new ChunkUploadResponse(true, uploadedChunks);
            response.setMessage("分片上传成功");
            return response;
            
        } catch (IOException e) {
            LOG.error("分片上传失败: identifier={}, chunkNumber={}, error={}", 
                request.getIdentifier(), request.getChunkNumber(), e.getMessage());
            throw new BusinessException(ResultCode.FILE_TRANSFER_ERROR);
        }
    }

    /**
     * 合并所有分片
     * @param module 模块
     * @param request 分片请求参数
     * @return 文件信息
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public FileInfo mergeChunks(FileSystemConstant.Module module, ChunkUploadRequest request) {
        try {
            String folderName = module.getFolderName();
            
            // 生成最终文件名和路径
            String fileName = request.getFilename();
            String fileSuffix = FileUtil.getFileSuffix(fileName);
            String fileNameWithoutExtension = FileUtil.getFileNameWithoutExtension(fileName);
            String finalFileName = FileUtil.concatenate(fileNameWithoutExtension, FileUtil.STANDARD_SPLICE, 
                UUIDUtils.uuid(), fileSuffix);
            
            String relativePath = FileUtil.concatenateWithSlash(folderName, finalFileName);
            String absolutePath = filePrefix + relativePath;
            
            // 创建目标文件
            File targetFile = new File(absolutePath);
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            
            // 合并分片
            mergeChunkFiles(request.getIdentifier(), request.getTotalChunks(), targetFile);
            
            // 删除分片临时文件
            deleteChunkFiles(request.getIdentifier());
            
            // 保存文件信息到数据库
            FileCtl fileCtl = new FileCtl();
            fileCtl.setFileName(fileName);
            fileCtl.setType(fileSuffix);
            fileCtl.setSize(FileUtil.getFileSizeDesc(request.getTotalSize()));
            fileCtl.setFilePath(relativePath);
            fileCtl.setModuleName(module.getModule());
            save(fileCtl);
            
            LOG.info("文件合并成功: identifier={}, fileId={}, fileName={}", 
                request.getIdentifier(), fileCtl.getId(), fileName);
            
            return new FileInfo(fileName, fileCtl.getId());
            
        } catch (Exception e) {
            LOG.error("文件合并失败: identifier={}, error={}", request.getIdentifier(), e.getMessage());
            // 清理临时文件
            try {
                deleteChunkFiles(request.getIdentifier());
            } catch (Exception ex) {
                LOG.error("清理临时文件失败: identifier={}", request.getIdentifier());
            }
            throw new BusinessException(ResultCode.FILE_TRANSFER_ERROR);
        }
    }

    /**
     * 验证分片请求参数
     */
    private void validateChunkRequest(ChunkUploadRequest request, MultipartFile chunk) {
        if (request == null || chunk == null || chunk.isEmpty()) {
            throw new BusinessException(ResultCode.MISS_PARAMETER);
        }
        if (StringUtils.isBlank(request.getIdentifier())) {
            throw new BusinessException(ResultCode.MISS_PARAMETER, "文件标识不能为空");
        }
        if (request.getChunkNumber() == null || request.getChunkNumber() < 1) {
            throw new BusinessException(ResultCode.MISS_PARAMETER, "分片序号无效");
        }
        if (request.getTotalChunks() == null || request.getTotalChunks() < 1) {
            throw new BusinessException(ResultCode.MISS_PARAMETER, "总分片数无效");
        }
        if (request.getChunkNumber() > request.getTotalChunks()) {
            throw new BusinessException(ResultCode.MISS_PARAMETER, "分片序号超出范围");
        }
    }

    /**
     * 获取分片文件夹路径
     */
    private String getChunkFolderPath(String identifier) {
        return FileUtil.concatenateWithSlash(filePrefix, "chunks", identifier);
    }

    /**
     * 获取分片文件路径
     */
    private String getChunkFilePath(String identifier, Integer chunkNumber) {
        String chunkFolder = getChunkFolderPath(identifier);
        String chunkFileName = FileUtil.generateChunkFilename(chunkNumber);
        return FileUtil.concatenateWithSlash(chunkFolder, chunkFileName);
    }

    /**
     * 合并分片文件
     */
    private void mergeChunkFiles(String identifier, Integer totalChunks, File targetFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            for (int i = 1; i <= totalChunks; i++) {
                String chunkFilePath = getChunkFilePath(identifier, i);
                File chunkFile = new File(chunkFilePath);
                
                if (!chunkFile.exists()) {
                    throw new BusinessException(ResultCode.FILE_NOT_FOUND, 
                        "分片文件不存在: " + i);
                }
                
                try (FileInputStream fis = new FileInputStream(chunkFile)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
            }
            fos.flush();
        }
    }

    /**
     * 删除分片临时文件
     */
    private void deleteChunkFiles(String identifier) throws IOException {
        String chunkFolderPath = getChunkFolderPath(identifier);
        File chunkFolder = new File(chunkFolderPath);
        
        if (chunkFolder.exists() && chunkFolder.isDirectory()) {
            FileUtil.delete(chunkFolderPath);
            LOG.info("分片临时文件已清理: identifier={}", identifier);
        }
    }
}
