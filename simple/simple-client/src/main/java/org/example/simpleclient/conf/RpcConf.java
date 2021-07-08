package org.example.simpleclient.conf;

import cn.hutool.core.util.ServiceLoaderUtil;
import org.example.nrpc.client.RpcClient;
import org.example.nrpc.client.proxy.BeanFactory;
import org.example.nrpc.register.api.RpcRegister;
import org.example.nrpc.simple.api.GoodsService;
import org.example.nrpc.simple.api.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 江南小俊
 * @since 2021/7/7
 **/
@Configuration
public class RpcConf implements ApplicationRunner {
    @Value("${nrpc.register.connectString}")
    private String connectString;

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public RpcClient rpcClient() {
        return new RpcClient();
    }

    @Bean(destroyMethod = "destroy")
    RpcRegister rpcRegister() {
        RpcRegister rpcRegister = ServiceLoaderUtil.loadFirstAvailable(RpcRegister.class);
        rpcRegister.init(connectString);
        return rpcRegister;
    }

    @Override
    public void run(ApplicationArguments args) {
        BeanFactory.setRpcClient(rpcClient());
        BeanFactory.setRpcRegister(rpcRegister());
        BeanFactory.addListener(OrderService.class.getName());
        BeanFactory.addListener(GoodsService.class.getName());
    }
}
