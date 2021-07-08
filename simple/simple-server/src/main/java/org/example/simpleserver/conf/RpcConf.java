package org.example.simpleserver.conf;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.ServiceLoaderUtil;
import org.example.nrpc.common.model.RpcAddress;
import org.example.nrpc.register.api.RpcRegister;
import org.example.nrpc.server.RpcServer;
import org.example.nrpc.server.util.ServiceManager;
import org.example.nrpc.simple.api.GoodsService;
import org.example.nrpc.simple.api.OrderService;
import org.example.simpleserver.impl.GoodsServiceImpl;
import org.example.simpleserver.impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ServiceLoader;

/**
 * @author 江南小俊
 * @since 2021/7/7
 **/
@Configuration
public class RpcConf implements ApplicationRunner {
    @Value("${nrpc.server.port}")
    private int port;
    @Value("${nrpc.server.host}")
    private String host;
    @Value("${nrpc.register.connectString}")
    private String connectString;

    @Bean(initMethod = "init", destroyMethod = "destroy")
    RpcServer rpcServer() {
        RpcServer rpcServer = new RpcServer(port);
        return rpcServer;
    }

    @Bean(destroyMethod = "destroy")
    RpcRegister rpcRegister() {
        RpcRegister rpcRegister = ServiceLoaderUtil.loadFirstAvailable(RpcRegister.class);
        rpcRegister.init(connectString);
        return rpcRegister;
    }

    void init() {
        ServiceManager.setRpcAddress(new RpcAddress(host, port));
        ServiceManager.setRpcRegister(rpcRegister());
        ServiceManager.register(OrderService.class, OrderServiceImpl.class);
        ServiceManager.register(GoodsService.class, GoodsServiceImpl.class);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        init();
        Thread thread = new Thread(rpcServer());
        thread.setName("-rpcServer-");
        thread.setDaemon(true);
        thread.start();
    }
}
