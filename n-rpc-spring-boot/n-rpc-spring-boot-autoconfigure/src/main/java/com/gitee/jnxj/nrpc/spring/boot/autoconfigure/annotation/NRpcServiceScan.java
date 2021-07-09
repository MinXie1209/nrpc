package com.gitee.jnxj.nrpc.spring.boot.autoconfigure.annotation;

import org.springframework.context.annotation.ComponentScans;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author 江南小俊
 * @since 2021/7/9
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface NRpcServiceScan {

    /**
     * 扫描的包
     */
    String[] value() default {};
}
