package org.example.simpleserver.conf;

import org.example.nrpc.server.RpcServer;
import org.example.nrpc.server.util.ServiceManager;
import org.example.nrpc.simple.api.OrderService;
import org.example.simpleserver.impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author 江南小俊
 * @since 2021/7/7
 **/
@Configuration
public class RpcConf implements ApplicationRunner {
    @Value("${nrpc.server.port}")
    private int port;

    @Bean(initMethod = "init", destroyMethod = "destroy")
    RpcServer rpcServer() {
        RpcServer rpcServer = new RpcServer(port);
        return rpcServer;
    }

    @PostConstruct
    void init() {
        ServiceManager.register(OrderService.class, OrderServiceImpl.class);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread thread = new Thread(rpcServer());
        thread.setName("-rpcServer-");
        thread.setDaemon(true);
        thread.start();
    }
}
