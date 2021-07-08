package org.example.nrpc.register.api.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author 江南小俊
 * @since 2021/7/8
 **/
@Data
@Builder
public class RpcServiceInstance {
    private String serviceName;
    private String address;
    private Integer port;
    private Object payload;
}
