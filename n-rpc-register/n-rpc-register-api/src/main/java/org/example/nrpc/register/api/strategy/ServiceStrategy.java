package org.example.nrpc.register.api.strategy;

import org.example.nrpc.register.api.model.RpcServiceInstance;

import java.util.List;
import java.util.Set;

/**
 * 服务策略
 *
 * @author 江南小俊
 * @since 2021/7/8
 **/
public interface ServiceStrategy {
    RpcServiceInstance getServiceInstance(List<RpcServiceInstance> list);
}
