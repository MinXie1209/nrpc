package org.example.simpleclient.conf;

import org.example.nrpc.client.RpcClient;
import org.example.nrpc.client.proxy.BeanFactory;
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
    @Value("${nrpc.server.host}")
    private String host;
    @Value("${nrpc.server.port}")
    private int port;

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public RpcClient rpcClient() {
        return new RpcClient("127.0.0.1", port);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        BeanFactory.setRpcClient(rpcClient());
        Thread thread = new Thread(rpcClient());
        thread.setName("-rpcClient-");
        thread.setDaemon(true);
        thread.start();
    }
}
