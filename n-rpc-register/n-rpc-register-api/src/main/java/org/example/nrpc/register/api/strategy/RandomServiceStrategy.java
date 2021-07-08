package org.example.nrpc.register.api.strategy;

import org.example.nrpc.register.api.model.RpcServiceInstance;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 随机获取服务实例
 *
 * @author 江南小俊
 * @since 2021/7/8
 **/
public class RandomServiceStrategy implements ServiceStrategy {
    private final Random random = new Random();

    @Override
    public RpcServiceInstance getServiceInstance(List<RpcServiceInstance> instances) {
        if (instances == null || instances.isEmpty()) {
            return null;
        }
        int thisIndex = random.nextInt(instances.size());
        return instances.get(thisIndex);
    }
}
