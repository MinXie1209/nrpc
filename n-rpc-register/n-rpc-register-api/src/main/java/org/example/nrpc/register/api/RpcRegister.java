package org.example.nrpc.register.api;

import org.example.nrpc.register.api.model.RpcServiceInstance;

/**
 * 服务注册接口
 *
 * @author 江南小俊
 * @since 2021/7/8
 **/
public interface RpcRegister {
    void init(String connectString);

    void register(RpcServiceInstance rpcServiceInstance) throws Exception;

    void destroy();
}
