package org.example.nrpc.client.proxy;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import org.example.nrpc.client.RpcClient;
import org.example.nrpc.client.proxy.interceptor.RpcMethodInterceptor;
import org.example.nrpc.common.model.RpcAddress;
import org.example.nrpc.register.api.RegisterConsumer;
import org.example.nrpc.register.api.RpcRegister;
import org.example.nrpc.register.api.model.RpcServiceInstance;
import org.example.nrpc.register.api.strategy.RandomServiceStrategy;
import org.example.nrpc.register.api.strategy.RoundRobinServiceStrategy;
import org.example.nrpc.register.api.strategy.ServiceStrategy;

import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * @author 江南小俊
 * @since 2021/7/6
 **/
@Data
@Slf4j
public class BeanFactory {
    private static Enhancer enhancer = new Enhancer();
    private static RpcClient rpcClient;
    //服务名 多个服务实例
    private static final Map<String, List<RpcServiceInstance>> serviceMap = new HashMap<>();
    //连接
    private static final Map<RpcAddress, Channel> channelMap = new HashMap<>();
    //Consumer
    private static final Map<RpcAddress, Consumer<Channel>> consumerMap = new HashMap<>();

    private static final Map<String, ServiceStrategy> strategyMap = new HashMap();

    public static void setRpcClient(RpcClient rpcClient) {
        BeanFactory.rpcClient = rpcClient;
    }

    public static <T> T getBean(Class<T> tClass) {
        enhancer.setSuperclass(tClass);
        //策略获取
        Channel channel = acquireChannelToStrategy(tClass.getName());
        enhancer.setCallback(new RpcMethodInterceptor(channel));
        return (T) enhancer.create();
    }

    private static Channel acquireChannelToStrategy(String serviceName) {
        //策略应该与服务名挂钩
        ServiceStrategy serviceStrategy = strategyMap.get(serviceName);
        if (serviceStrategy == null) {
            throw new RuntimeException("找不到服务实例:" + serviceName);
        }
        RpcServiceInstance serviceInstance = serviceStrategy.getServiceInstance(serviceMap.get(serviceName));
        if (serviceInstance == null) {
            throw new RuntimeException("找不到服务实例:" + serviceName);
        }
        Channel channel = channelMap.get(new RpcAddress(serviceInstance.getAddress(), serviceInstance.getPort()));
        if (channel == null) throw new RuntimeException("找不到可用连接");
        return channel;
    }

    public static void addRegister(RpcRegister rpcRegister) {
        rpcRegister.addListener(new RegisterConsumer<RpcServiceInstance>() {

            @Override
            public void accept(RpcServiceInstance rpcServiceInstance) {
                log.debug("添加服务实例:{}", rpcServiceInstance);
                addServiceInstance(rpcServiceInstance);
            }

            @Override
            public void cancel(RpcServiceInstance rpcServiceInstance) {
                log.debug("删除服务实例：{}", rpcServiceInstance);
                removeServiceInstance(rpcServiceInstance);
            }
        });
    }

    private static void removeServiceInstance(RpcServiceInstance rpcServiceInstance) {
        synchronized (serviceMap) {
            if (serviceMap.containsKey(rpcServiceInstance.getServiceName())) {
                boolean res = serviceMap.get(rpcServiceInstance.getServiceName()).removeIf(item ->
                        item.getAddress().equals(rpcServiceInstance.getAddress()) && item.getPort().equals(rpcServiceInstance.getPort())
                );
                log.debug("{}", res ? "删除成功" : "删除失败");
            }
        }
        synchronized (channelMap) {
            RpcAddress rpcAddress = new RpcAddress(rpcServiceInstance.getAddress(), rpcServiceInstance.getPort());
            if (channelMap.containsKey(rpcAddress)) {
                Channel channel = channelMap.remove(rpcAddress);
                channel.close();
                log.debug("关闭channel");
            }
        }
    }

    /**
     * 添加服务实例
     *
     * @param rpcServiceInstance
     * @return void
     * @author Jim
     * @since 2021/7/8 下午2:05
     **/

    private static void addServiceInstance(RpcServiceInstance rpcServiceInstance) {
        synchronized (serviceMap) {
            if (serviceMap.containsKey(rpcServiceInstance.getServiceName())) {
                serviceMap.get(rpcServiceInstance.getServiceName()).add(rpcServiceInstance);
            } else {
                List<RpcServiceInstance> list = new CopyOnWriteArrayList<>();
                list.add(rpcServiceInstance);
                serviceMap.put(rpcServiceInstance.getServiceName(), list);
                //添加服务获取策略
                strategyMap.put(rpcServiceInstance.getServiceName(), new RoundRobinServiceStrategy());
            }
        }
        synchronized (channelMap) {
            RpcAddress rpcAddress = new RpcAddress(rpcServiceInstance.getAddress(), rpcServiceInstance.getPort());
            //连接
            if (!channelMap.containsKey(rpcAddress)) {
                channelMap.put(rpcAddress, null);
                Consumer<Channel> consumer = channel -> {
                    channelMap.put(rpcAddress, channel);
                };
                rpcClient.run(consumer, rpcAddress);
                consumerMap.put(rpcAddress, consumer);
            }
        }
    }


    /**
     * 连接断开了
     *
     * @param channel
     * @return void
     * @author Jim
     * @since 2021/7/8 下午3:15
     **/

    public static void channelInactive(Channel channel) {
        //是否要重新连接
        synchronized (channelMap) {
            if (channelMap.containsValue(channel)) {
                consumerMap.forEach((k, v) -> {
                    if (v.equals(channel)) {
                        //重新连接
                        channelMap.put(k, null);
                        rpcClient.run(consumerMap.get(k), k);
                    }
                });
            }
        }

    }
}
