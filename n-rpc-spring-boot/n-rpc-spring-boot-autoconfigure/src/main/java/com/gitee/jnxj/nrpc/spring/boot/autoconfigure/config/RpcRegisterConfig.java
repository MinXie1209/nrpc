package com.gitee.jnxj.nrpc.spring.boot.autoconfigure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 江南小俊
 * @since 2021/7/9
 **/
@Data
@ConfigurationProperties("nrpc.register")
public class RpcRegisterConfig {
    /**
     * 服务注册地址
     * 示例：
     * 1.使用Zookeeper作为注册中心，connectString=“127.0.0.1:2181” <br/>
     * 2.使用Nacos作为注册中心，connectString=“127.0.0.1:8848”
     **/
    private String connectString;


}
