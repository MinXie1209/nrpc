package org.example.nrpc.register.api;

import org.example.nrpc.register.api.model.RpcServiceInstance;

import java.util.List;
import java.util.function.Consumer;

/**
 * 服务注册接口
 *
 * @author 江南小俊
 * @since 2021/7/8
 **/
public interface RpcRegister {
    void init(String connectString);

    void register(RpcServiceInstance rpcServiceInstance) throws Exception;

//    void addListener(RegisterConsumer<RpcServiceInstance> consumer);

    void addListener(String serviceName, RegisterConsumer<RpcServiceInstance> consumer);

    void destroy();
}
