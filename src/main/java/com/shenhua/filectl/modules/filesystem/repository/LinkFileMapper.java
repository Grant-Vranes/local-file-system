package com.shenhua.filectl.modules.filesystem.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.shenhua.filectl.modules.filesystem.domain.LinkFile;
import com.shenhua.filectl.modules.filesystem.response.FileInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hshi22
 * @since 2023-11-24
 */
public interface LinkFileMapper extends BaseMapper<LinkFile> {

    List<FileInfo> getFileInfoByLinkId(@Param("module") String module, @Param("linkId") String linkId);

    int checkEquipmentAttachFileChangedAnotherSource(@Param("module") String module,
                                                     @Param("linkId") String linkId,
                                                     @Param("nowFileId") String nowFileId);

    List<String> getFileIdsByCondition(@Param(Constants.WRAPPER) LambdaQueryWrapper<LinkFile> wr);

}
