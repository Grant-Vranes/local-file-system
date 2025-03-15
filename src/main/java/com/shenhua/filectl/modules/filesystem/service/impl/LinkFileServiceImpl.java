package com.shenhua.filectl.modules.filesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shenhua.filectl.common.constants.FileSystemConstant;
import com.shenhua.filectl.modules.filesystem.domain.LinkFile;
import com.shenhua.filectl.modules.filesystem.repository.LinkFileMapper;
import com.shenhua.filectl.modules.filesystem.response.FileInfo;
import com.shenhua.filectl.modules.filesystem.service.ILinkFileService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 各个模块的表ID与文件ID的关联 服务实现类
 * </p>
 *
 * @author hshi22
 * @since 2023-11-24
 */
@Service
public class LinkFileServiceImpl extends ServiceImpl<LinkFileMapper, LinkFile> implements ILinkFileService {

    @Resource
    private LinkFileMapper linkFileMapper;

    /**
     * 根据模块和linkId获得对应文件
     * @param module
     * @param linkId
     * @return
     */
    @Override
    public List<FileInfo> getFileInfoByLinkId(FileSystemConstant.Module module, String linkId) {
        return linkFileMapper.getFileInfoByLinkId(module.getModule(), linkId);
    }

    /**
     * 根据模块和 关联表id 删除对应文件关联
     * @param module
     * @param linkId
     * @return
     */
    @Override
    public Integer deleteByLinkId(FileSystemConstant.Module module, String linkId) {
        LambdaQueryWrapper<LinkFile> linkFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        linkFileLambdaQueryWrapper.eq(LinkFile::getLinkId, linkId)
                .eq(LinkFile::getModuleName, module.getModule());
        return linkFileMapper.delete(linkFileLambdaQueryWrapper);
    }

    /**
     * 根据模块和 关联id集合 删除对应文件
     * @param module
     * @param linkIds
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteBatchByLinkIds(FileSystemConstant.Module module, List<String> linkIds) {
        if (CollectionUtils.isEmpty(linkIds)) {
            return 0;
        }
        LambdaQueryWrapper<LinkFile> linkFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        linkFileLambdaQueryWrapper.in(LinkFile::getLinkId, linkIds)
                .eq(LinkFile::getModuleName, module.getModule());
        return linkFileMapper.delete(linkFileLambdaQueryWrapper);
    }

    /**
     * 建立文件关联
     * @param module 文件所存储的模块
     * @param linkId 文件关联到的linkId，它可以是任何相关表的主键id
     * @param fileIds 需要被关联到的文件id集合
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean establishFileAssociation(FileSystemConstant.Module module, String linkId, List<String> fileIds) {
        if (CollectionUtils.isEmpty(fileIds)) {
            // throw new BusinessException(ResultCode.XXX);
            // 一般在外部会做fileIds的校验，这里再校验还是empty就return false
            return false;
        }
        List<LinkFile> needSaveLinkFiles = new ArrayList<>();
        fileIds.forEach(fileId -> needSaveLinkFiles.add(new LinkFile(module.getModule(), linkId, fileId)));
        return this.saveBatch(needSaveLinkFiles);
    }

    /**
     * 批量建立文件关联
     * linkIds集合中的每一个id都会关联到一批 fileIds
     * @param module 文件所存储的模块
     * @param linkIds 文件关联到的linkId集合，它可以是任何相关表的主键id
     * @param fileIds 需要被关联到的文件id集合
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean establishFileAssociationBatch(FileSystemConstant.Module module, List<String> linkIds, List<String> fileIds) {
        if (CollectionUtils.isEmpty(fileIds)) {
            // throw new BusinessException(ResultCode.XXX);
            // 一般在外部会做fileIds的校验，这里再校验还是empty就return false
            return false;
        }
        List<LinkFile> needSaveLinkFiles = new ArrayList<>();
        linkIds.forEach(linkId -> {
            fileIds.forEach(fileId -> needSaveLinkFiles.add(new LinkFile(module.getModule(), linkId, fileId)));
        });
        return this.saveBatch(needSaveLinkFiles);
    }

    /**
     * 通过module和linkId集合查找对应的 文件id集合
     * @param module
     * @param linkIds
     * @return
     */
    @Override
    public List<String> getFileIdsByLinkIds(FileSystemConstant.Module module, List<String> linkIds) {
        if (CollectionUtils.isEmpty(linkIds)) {
            return null;
        }
        LambdaQueryWrapper<LinkFile> wr = new LambdaQueryWrapper<>();
        wr.eq(LinkFile::getModuleName, module.getModule()).in(LinkFile::getLinkId, linkIds);
        return linkFileMapper.getFileIdsByCondition(wr);
    }

    /**
     * 通过module和linkId查找对应的 文件id集合
     * @param module
     * @param linkId
     * @return
     */
    @Override
    public List<String> getFileIdsByLinkId(FileSystemConstant.Module module, String linkId) {
        LambdaQueryWrapper<LinkFile> wr = new LambdaQueryWrapper<>();
        wr.eq(LinkFile::getModuleName, module.getModule()).eq(LinkFile::getLinkId, linkId);
        return linkFileMapper.getFileIdsByCondition(wr);
    }

}
