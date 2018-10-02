package com.github.wallet.restwebservice.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimit {

    int limit() default Integer.MAX_VALUE;
    long duration() default 1;
    TimeUnit unit() default TimeUnit.MINUTES;
}
