package org.hjf.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户登录状态检查注解
 * Class 文件有效
 * RetentionPolicy: 注解声明周期 SOURCE CLASS RUNTIME
 **/
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface LoginCheck {
}
