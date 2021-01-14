package com.common.util.tj.annotation;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

/**
 * @author
 * @Description: 文件描述
 * @date
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface RequestLimit {
    /**
     * 允许访问的次数
     */
    int count() default 5;

    /**
     * 时间段，多少时间段内运行访问count次
     */
    long time() default 60000;
}
