package com.gitee.jnxj.nrpc.spring.boot.autoconfigure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 江南小俊
 * @since 2021/7/9
 **/
@Data
@ConfigurationProperties("nrpc.server")
public class RpcServerConfig {
    /**
     * 服务暴露端口 默认9999
     **/
    private Integer port = 9999;
    /**
     * 服务暴露地址
     **/
    private String address;

}
