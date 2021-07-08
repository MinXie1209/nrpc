package org.example.nrpc.register;

import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.example.nrpc.register.api.RegisterConsumer;
import org.example.nrpc.register.api.RpcRegister;
import org.example.nrpc.register.api.model.RpcServiceInstance;


/**
 * Zookeeper实现服务注册
 *
 * @author 江南小俊
 * @since 2021/7/8
 **/
@Slf4j
@AutoService(RpcRegister.class)
public class ZookeeperRegister implements RpcRegister {
    private CuratorFramework client;
    private ServiceDiscovery<Object> discovery;
    private String connectString;
    private final String BASE_PATH = "/n-rpc";

    @Override
    public void init(String connectString) {
        this.connectString = connectString;
        //永久重连 间隔1s
        RetryForever retryForever = new RetryForever(1000);
        client = CuratorFrameworkFactory.newClient(connectString, retryForever);
        client.start();
        log.debug("ZookeeperRegister  client start...");
        discovery = ServiceDiscoveryBuilder.builder(Object.class).client(client).basePath(
                BASE_PATH).build();
        try {
            discovery.start();
            log.info("ZookeeperRegister  discovery start...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(RpcServiceInstance rpcServiceInstance) throws Exception {
        log.debug("register:{}", rpcServiceInstance);
        ServiceInstanceBuilder<Object> builder = ServiceInstance.builder();
        ServiceInstance<Object> rpcService = builder.name(rpcServiceInstance.getServiceName())
                .address(rpcServiceInstance.getAddress())
                .port(rpcServiceInstance.getPort())
                .payload(rpcServiceInstance.getPayload())
                .build();
        discovery.registerService(rpcService);
    }

//    @Override
//    public void addListener(RegisterConsumer<RpcServiceInstance> consumer) {
////        CuratorCache cache = CuratorCache.builder(client, BASE_PATH).build();
////        cache.listenable().addListener((type, oldData, data) -> {
////            log.debug("监听节点变化:-{}-:{}->{}", type, oldData, data);
////            //拿到新数据进行处理
////            //oldData-->data    (null--->data)新增
////            //oldData-->data    (oldData--->null)删除
////            if (data == null) {
////                //取前面的data
////                try {
////                    //data==[]? 没有数据新增
////                    if (oldData.getData().length == 0) {
////                        return;
////                    }
////                    ServiceInstance serviceInstance =
////                            new JsonInstanceSerializer(Object.class).deserialize(oldData.getData());
////                    log.debug("旧数据:{}", serviceInstance);
////                    consumer.cancel(RpcServiceInstance.builder()
////                            .serviceName(serviceInstance.getName())
////                            .address(serviceInstance.getAddress())
////                            .port(serviceInstance.getPort())
////                            .payload(serviceInstance.getPayload())
////                            .build());
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
////            } else {
////                if (data.getData().length == 0) {
////                    return;
////                }
////                //构造一个map
////                try {
////                    ServiceInstance serviceInstance =
////                            new JsonInstanceSerializer(Object.class).deserialize(data.getData());
////                    log.debug("新数据:{}", serviceInstance);
////                    consumer.accept(RpcServiceInstance.builder()
////                            .serviceName(serviceInstance.getName())
////                            .address(serviceInstance.getAddress())
////                            .port(serviceInstance.getPort())
////                            .payload(serviceInstance.getPayload())
////                            .build());
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
////            }
////        });
////        cache.start();
////        log.debug("开始监听节点变化");
//    }

    @Override
    public void addListener(String serviceName, RegisterConsumer<RpcServiceInstance> consumer) {
        CuratorCache cache = CuratorCache.builder(client, BASE_PATH + "/" + serviceName).build();
        cache.listenable().addListener((type, oldData, data) -> {
            log.debug("监听节点变化:-{}-:{}->{}", type, oldData, data);
            //拿到新数据进行处理
            //oldData-->data    (null--->data)新增
            //oldData-->data    (oldData--->null)删除
            if (data == null) {
                //取前面的data
                try {
                    //data==[]? 没有数据新增
                    if (oldData.getData().length == 0) {
                        return;
                    }
                    ServiceInstance serviceInstance =
                            new JsonInstanceSerializer(Object.class).deserialize(oldData.getData());
                    log.debug("旧数据:{}", serviceInstance);
                    consumer.cancel(RpcServiceInstance.builder()
                            .serviceName(serviceInstance.getName())
                            .address(serviceInstance.getAddress())
                            .port(serviceInstance.getPort())
                            .payload(serviceInstance.getPayload())
                            .build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (data.getData().length == 0) {
                    return;
                }
                //构造一个map
                try {
                    ServiceInstance serviceInstance =
                            new JsonInstanceSerializer(Object.class).deserialize(data.getData());
                    log.debug("新数据:{}", serviceInstance);
                    consumer.accept(RpcServiceInstance.builder()
                            .serviceName(serviceInstance.getName())
                            .address(serviceInstance.getAddress())
                            .port(serviceInstance.getPort())
                            .payload(serviceInstance.getPayload())
                            .build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        cache.start();
        log.debug("开始监听节点变化");
    }

    @Override
    public void destroy() {
        log.debug("ZookeeperRegister close...");
        client.close();
    }
}
