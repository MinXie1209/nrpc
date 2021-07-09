package com.gitee.jnxj.nrpc.spring.boot.autoconfigure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 江南小俊
 * @since 2021/7/9
 **/
@Data
@ConfigurationProperties("nrpc.client")
public class RpcClientConfig {
    /**
     * 是否开启客户端 默认关闭
     **/

    private Boolean enabled;
}
