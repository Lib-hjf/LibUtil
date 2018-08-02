package org.hjf.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Android 6.0 权限检查和请求
 * RUNTIME 运行时有效
 * RetentionPolicy: 注解声明周期 SOURCE CLASS RUNTIME
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionCheck {
    String[] value();
}
