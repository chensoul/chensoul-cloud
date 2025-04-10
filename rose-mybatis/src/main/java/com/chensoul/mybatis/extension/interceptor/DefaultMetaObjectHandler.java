package com.chensoul.mybatis.extension.interceptor;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.chensoul.core.spring.WebUtils;
import java.time.LocalDateTime;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.util.ClassUtils;

/**
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
public class DefaultMetaObjectHandler implements MetaObjectHandler {

    /**
     * 填充值，先判断是否有手动设置，优先手动设置的值，例如：job必须手动设置
     *
     * @param fieldName  属性名
     * @param fieldVal   属性值
     * @param metaObject MetaObject
     * @param isCover    是否覆盖原有值,避免更新操作手动入参
     */
    protected static void fillValIfNullByName(
            String fieldName, Object fieldVal, MetaObject metaObject, boolean isCover) {
        // 1. 没有 set 方法
        if (!metaObject.hasSetter(fieldName)) {
            return;
        }
        // 2. 如果用户有手动设置的值
        if (metaObject.getValue(fieldName) != null && !isCover) {
            return;
        }
        // 3. 类型相同时设置
        Class<?> getterType = metaObject.getGetterType(fieldName);
        if (ClassUtils.isAssignableValue(getterType, fieldVal)) {
            metaObject.setValue(fieldName, fieldVal);
        }
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        fillValIfNullByName("createdBy", WebUtils.getUsername(), metaObject, false);
        fillValIfNullByName("creator", WebUtils.getUsername(), metaObject, false);

        fillValIfNullByName("createTime", LocalDateTime.now(), metaObject, false);

        fillValIfNullByName("deleted", false, metaObject, true);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        fillValIfNullByName("updatedBy", WebUtils.getUsername(), metaObject, false);
        fillValIfNullByName("updater", WebUtils.getUsername(), metaObject, false);

        fillValIfNullByName("updateTime", LocalDateTime.now(), metaObject, true);
    }
}
