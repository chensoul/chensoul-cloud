package com.chensoul.spring.boot.aspect;

import static com.chensoul.core.CommonConstants.PROFILE_NOT_PROD;

import com.chensoul.core.jackson.JacksonUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;

/**
 * Aspect for logging execution of util and repository Spring components.
 * <p>
 * By default, it only runs with the "dev" profile.
 */
@Profile(PROFILE_NOT_PROD)
@Configuration
@Aspect
@EnableAspectJAutoProxy
public class LoggingAspect {

    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Pointcut("within(@org.springframework.stereotype.Repository *)"
            + " || within(@org.springframework.stereotype.Service *)"
            + " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the
        // advices.
    }

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut("within(com.chensoul..*.*Repository)" + " || within(com.chensoul..*.*Service)"
            + " || within(com.chensoul..*.*Controller)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the
        // advices.
    }

    /**
     * Retrieves the {@link Logger} associated to the given {@link JoinPoint}.
     *
     * @param joinPoint join point we want the logger for.
     * @return {@link Logger} associated to the given {@link JoinPoint}.
     */
    private Logger logger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
    }

    /**
     * Advice that logs methods throwing exceptions.
     *
     * @param joinPoint join point for advice.
     * @param e         exception.
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        logger(joinPoint)
                .error(
                        "Exception in {}() with cause = '{}' and exception = '{}'",
                        joinPoint.getSignature().getName(),
                        e.getCause() != null ? String.valueOf(e.getCause()) : "NULL",
                        e.getMessage(),
                        e);
    }

    /**
     * Advice that logs when a method is entered and exited.
     *
     * @param joinPoint join point for advice.
     * @return result.
     * @throws Throwable throws {@link IllegalArgumentException}.
     */
    @Around("applicationPackagePointcut() && springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger log = logger(joinPoint);
        if (log.isDebugEnabled()) {
            log.debug(
                    "Enter {}() with arguments = {}",
                    joinPoint.getSignature().getName(),
                    JacksonUtils.toString(joinPoint.getArgs()));
        }
        try {
            Object result = joinPoint.proceed();
            if (log.isDebugEnabled()) {
                log.debug(
                        "Exit {}() with result = {}",
                        joinPoint.getSignature().getName(),
                        JacksonUtils.toString(result));
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error(
                    "Illegal argument: {} in {}()",
                    JacksonUtils.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getName());
            throw e;
        }
    }
}
