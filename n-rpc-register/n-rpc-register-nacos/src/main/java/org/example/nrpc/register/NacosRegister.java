package org.example.nrpc.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.example.nrpc.register.api.RegisterConsumer;
import org.example.nrpc.register.api.RpcRegister;
import org.example.nrpc.register.api.model.RpcServiceInstance;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Nacos实现服务注册
 *
 * @author 江南小俊
 * @since 2021/7/8
 **/
@Slf4j
@AutoService(RpcRegister.class)
public class NacosRegister implements RpcRegister {
    private NamingService service;
    private final String clusterName = "/n-rpc";

    @Override
    public void init(String connectString) {
        try {
            service = NamingFactory.createNamingService(connectString);
            log.debug("NacosRegister init...");
        } catch (NacosException e) {
            e.printStackTrace();
            log.error("", e);
        }
    }

    @Override
    public void register(RpcServiceInstance rpcServiceInstance) throws Exception {
        service.registerInstance(rpcServiceInstance.getServiceName(),
                rpcServiceInstance.getAddress(),
                rpcServiceInstance.getPort(), clusterName);
    }


    @Override
    public void addListener(String serviceName, RegisterConsumer<RpcServiceInstance> consumer) {
        log.debug("addListener:{}", serviceName);
        try {
            service.subscribe(serviceName, event -> {
                log.debug("onEvent:{}", event);
                if (NamingEvent.class.isAssignableFrom(event.getClass())) {
                    NamingEvent namingEvent = (NamingEvent) event;
                    List<RpcServiceInstance> rpcServiceInstances = new CopyOnWriteArrayList<>();
                    for (Instance instance : namingEvent.getInstances()) {
                        rpcServiceInstances.add(RpcServiceInstance.builder()
                                .serviceName(serviceName)
                                .address(instance.getIp())
                                .port(instance.getPort())
                                .build());
                    }
                    consumer.updateAll(rpcServiceInstances);
                }
            });
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        try {
            service.shutDown();
            log.debug("NacosRegister shutdown...");
        } catch (NacosException e) {
            e.printStackTrace();
            log.error("", e);
        }
    }
}
