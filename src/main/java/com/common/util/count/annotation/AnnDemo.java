package com.common.util.count.annotation;

import java.lang.annotation.*;

/**
 * @author
 * @date
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AnnDemo {
    String value();

    boolean isAop() default true;
}