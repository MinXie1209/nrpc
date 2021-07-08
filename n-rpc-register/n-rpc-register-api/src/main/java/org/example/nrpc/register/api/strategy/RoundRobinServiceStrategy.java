package org.example.nrpc.register.api.strategy;

import org.example.nrpc.register.api.model.RpcServiceInstance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询服务实例
 *
 * @author 江南小俊
 * @since 2021/7/8
 **/
public class RoundRobinServiceStrategy implements ServiceStrategy {
    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public RpcServiceInstance getServiceInstance(List<RpcServiceInstance> list) {
        return list.get(Math.abs(index.getAndIncrement()) % list.size());
    }
}
