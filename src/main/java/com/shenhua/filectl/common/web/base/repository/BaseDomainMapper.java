package com.shenhua.filectl.common.web.base.repository;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shenhua.filectl.common.web.base.domain.BaseDomain;
import org.apache.commons.lang3.StringUtils;

/**
 * 配合BaseDomainService使用
 */
public interface BaseDomainMapper extends BaseMapper<BaseDomain> {
}
