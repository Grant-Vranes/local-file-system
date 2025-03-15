package com.shenhua.filectl.modules.filesystem.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.shenhua.filectl.common.constants.FileSystemConstant;
import com.shenhua.filectl.modules.filesystem.domain.LinkFile;
import com.shenhua.filectl.modules.filesystem.response.FileInfo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hshi22
 * @since 2023-11-24
 */
public interface ILinkFileService extends IService<LinkFile> {

    List<FileInfo> getFileInfoByLinkId(FileSystemConstant.Module module, String linkId);

    Integer deleteByLinkId(FileSystemConstant.Module module, String linkId);

    Integer deleteBatchByLinkIds(FileSystemConstant.Module module, List<String> linkIds);

    Boolean establishFileAssociation(FileSystemConstant.Module module, String linkId, List<String> fileIds);

    Boolean establishFileAssociationBatch(FileSystemConstant.Module module, List<String> linkIds, List<String> fileIds);

    List<String> getFileIdsByLinkIds(FileSystemConstant.Module module, List<String> linkIds);

    List<String> getFileIdsByLinkId(FileSystemConstant.Module module, String linkId);

}
