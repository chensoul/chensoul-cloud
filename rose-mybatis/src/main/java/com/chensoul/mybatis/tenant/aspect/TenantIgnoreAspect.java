package com.chensoul.mybatis.tenant.aspect;

import com.chensoul.mybatis.tenant.annotation.TenantIgnore;
import com.chensoul.mybatis.tenant.util.TenantUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 忽略多租户的 Aspect，基于 {@link TenantIgnore} 注解实现，用于一些全局的逻辑。 例如说，一个定时任务，读取所有数据，进行处理。
 * 又例如说，读取所有数据，进行缓存。
 * <p>
 * 整体逻辑的实现，和 {@link TenantUtils#executeIgnore(CheckedRunnable)} 需要保持一致
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 */
@Aspect
@Slf4j
public class TenantIgnoreAspect {

    @Around("@annotation(tenantIgnore)")
    public Object around(ProceedingJoinPoint joinPoint, TenantIgnore tenantIgnore) throws Throwable {
        return TenantUtils.executeIgnore(() -> joinPoint.proceed());
    }
}
