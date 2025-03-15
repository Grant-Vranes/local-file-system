package com.shenhua.filectl.common.myabtis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.shenhua.filectl.common.constants.GlobalConstant;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 修改为mybatis-plus 3.3.0以上才支持的严格自动填充
     * 当原字段有数据的时候，不填充
     * 当要用来填充的数据为null,也不填充
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createBy", String.class, "");
        this.strictInsertFill(metaObject, "createName", String.class, "");
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateBy", String.class, "");
        this.strictInsertFill(metaObject, "updateName", String.class, "");
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "deleted", Integer.class, GlobalConstant.Deleted_Flag.NOT_DELETE.getIntVal());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateBy", String.class, "");
        this.strictUpdateFill(metaObject, "updateName", String.class, "");
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
