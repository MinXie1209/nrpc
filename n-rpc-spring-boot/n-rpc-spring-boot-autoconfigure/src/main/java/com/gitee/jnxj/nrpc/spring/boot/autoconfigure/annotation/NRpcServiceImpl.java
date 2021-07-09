package com.gitee.jnxj.nrpc.spring.boot.autoconfigure.annotation;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * 暴露服务接口
 *
 * @author 江南小俊
 * @since 2021/7/9
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@Component
public @interface NRpcServiceImpl {
    /**
     * 指定实现的服务接口
     **/

    Class<?> value() default Void.class;
}
