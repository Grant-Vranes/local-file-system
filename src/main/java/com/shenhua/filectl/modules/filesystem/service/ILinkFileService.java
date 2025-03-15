package com.shenhua.filectl.modules.filesystem.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.shenhua.filectl.common.constants.FileSystemConstant;
import com.shenhua.filectl.modules.filesystem.domain.LinkFile;
import com.shenhua.filectl.modules.filesystem.response.FileInfo;

import java.util.List;

/**
 * <p>
 * 各个模块的表ID与文件ID的关联 服务类
 * </p>
 *
 * @author hshi22
 * @since 2023-11-24
 */
public interface ILinkFileService extends IService<LinkFile> {

    /**
     * 根据模块和linkId获得对应文件
     * @param module
     * @param linkId
     * @return
     */
    List<FileInfo> getFileInfoByLinkId(FileSystemConstant.Module module, String linkId);

    /**
     * 根据模块和 关联表id 删除对应文件关联
     * @param module
     * @param linkId
     * @return
     */
    Integer deleteByLinkId(FileSystemConstant.Module module, String linkId);

    /**
     * 根据模块和 关联id集合 删除对应文件
     * @param module
     * @param linkIds
     * @return
     */
    Integer deleteBatchByLinkIds(FileSystemConstant.Module module, List<String> linkIds);

    /**
     * 建立文件关联
     * @param module 文件所存储的模块
     * @param linkId 文件关联到的linkId，它可以是任何相关表的主键id
     * @param fileIds 需要被关联到的文件id集合
     * @return
     */
    Boolean establishFileAssociation(FileSystemConstant.Module module, String linkId, List<String> fileIds);

    /**
     * 批量建立文件关联
     * linkIds集合中的每一个id都会关联到一批 fileIds
     * @param module 文件所存储的模块
     * @param linkIds 文件关联到的linkId集合，它可以是任何相关表的主键id
     * @param fileIds 需要被关联到的文件id集合
     * @return
     */
    Boolean establishFileAssociationBatch(FileSystemConstant.Module module, List<String> linkIds, List<String> fileIds);

    /**
     * 通过module和linkId集合查找对应的 文件id集合
     * @param module
     * @param linkIds
     * @return
     */
    List<String> getFileIdsByLinkIds(FileSystemConstant.Module module, List<String> linkIds);

    /**
     * 通过module和linkId查找对应的 文件id集合
     * @param module
     * @param linkId
     * @return
     */
    List<String> getFileIdsByLinkId(FileSystemConstant.Module module, String linkId);

}
