package org.example.register.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.example.nrpc.register.api.RegisterConsumer;
import org.example.nrpc.register.api.RpcRegister;
import org.example.nrpc.register.api.model.RpcServiceInstance;

/**
 * @author 江南小俊
 * @since 2021/7/8
 **/
//@AutoService(RpcRegister.class)
@Slf4j
public class NacosRegister implements RpcRegister {
    private NamingService service;
    private final String clusterName = "/n-rpc";

    @Override
    public void init(String connectString) {
        try {
            service = NamingFactory.createNamingService(connectString);
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
    public void addListener(RegisterConsumer<RpcServiceInstance> consumer) {
        try {
            log.debug("addListener:{}", service.getSubscribeServices());
        } catch (NacosException e) {
            e.printStackTrace();
        }
//        service.subscribe("org.example.nrpc.simple.api.GoodsService", new EventListener() {
//            @Override
//            public void onEvent(Event event) {
//                System.out.println(((NamingEvent)event).getServiceName());
//                System.out.println(((NamingEvent)event).getInstances());
//            }
//        });

    }

    @Override
    public void destroy() {
        try {
            service.shutDown();
        } catch (NacosException e) {
            e.printStackTrace();
            log.error("", e);
        }
    }
}
