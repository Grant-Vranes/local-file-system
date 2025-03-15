package com.shenhua.filectl.common.web.base.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.collect.Lists;
import com.shenhua.filectl.common.constants.GlobalConstant;
import com.shenhua.filectl.common.web.base.domain.BaseDomain;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BaseDomainService<S extends BaseDomain> extends IService<S> {

    /**
     * 逻辑删除
     * @param id
     */
    default boolean deleteLogicById(String id) {
        if (StringUtils.isBlank(id)) return true;

        LambdaUpdateWrapper<S> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(S::getDeleted, GlobalConstant.Deleted_Flag.DELETED.getIntVal())
                .eq(S::getId, id);
        return this.update(updateWrapper);
    }

    /**
     * 逻辑删除 批量
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean deleteLogicByIds(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) return true;

        boolean allSuccess = true;
        LambdaUpdateWrapper<S> updateWrapper = new LambdaUpdateWrapper<>();
        // 可能超过sqlserver限制的2100，所以分批处理数据
        for (List<String> partIdList : Lists.partition(ids, 1000)) {
            // clear wrapper
            updateWrapper.clear();
            // package wrapper
            updateWrapper.set(S::getDeleted, GlobalConstant.Deleted_Flag.DELETED.getIntVal())
                    .in(S::getId, partIdList);
            allSuccess = this.update(updateWrapper);
        }

        if (!allSuccess) throw new RuntimeException("deleteLogicByIds failed, and will rollback");
        return allSuccess;
    }

    /**
     * 检查输入id是否在表中存在
     * @param id
     * @return
     */
    default boolean existsById(String id) {

        return StringUtils.isNotBlank(id) &&
                this.count(new LambdaQueryWrapper<S>().eq(S::getId, id)
                        .eq(S::getDeleted, GlobalConstant.Deleted_Flag.NOT_DELETE.getIntVal())) > 0;
    }
}
