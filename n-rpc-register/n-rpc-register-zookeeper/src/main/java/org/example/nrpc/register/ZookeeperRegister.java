package org.example.nrpc.register;

import com.google.auto.service.AutoService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
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

    @Override
    public void init(String connectString) {
        this.connectString = connectString;
        //永久重连 间隔1s
        RetryForever retryForever = new RetryForever(1000);
        client = CuratorFrameworkFactory.newClient(connectString, retryForever);
        client.start();
        log.debug("ZookeeperRegister  client start...");
        discovery = ServiceDiscoveryBuilder.builder(Object.class).client(client).basePath(
                "/n-rpc").build();
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

    @Override
    public void destroy() {
        log.debug("ZookeeperRegister close...");
        client.close();
    }
}
