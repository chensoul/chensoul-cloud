package com.chensoul.spring.boot.audit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Audit {
    String value() default "";

    String resourceResolverName() default "RequestAsStringResourceResolver";

    String actionResolverName() default "DefaultAuditActionResolver";

    String principalResolverName() default "SpringSecurityPrincipalResolver";

}