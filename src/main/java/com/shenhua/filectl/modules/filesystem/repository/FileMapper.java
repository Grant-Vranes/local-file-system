package com.shenhua.filectl.modules.filesystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shenhua.filectl.modules.filesystem.domain.FileCtl;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * ???? Mapper 接口
 * </p>
 *
 * @author hshi22
 * @since 2023-11-23
 */
@Mapper
public interface FileMapper extends BaseMapper<FileCtl> {

}
