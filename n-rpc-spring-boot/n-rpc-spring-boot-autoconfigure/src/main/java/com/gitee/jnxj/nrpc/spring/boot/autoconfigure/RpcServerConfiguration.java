package com.gitee.jnxj.nrpc.spring.boot.autoconfigure;

import cn.hutool.core.util.ServiceLoaderUtil;
import com.gitee.jnxj.nrpc.spring.boot.autoconfigure.annotation.NRpcServiceImpl;
import com.gitee.jnxj.nrpc.spring.boot.autoconfigure.config.RpcRegisterConfig;
import com.gitee.jnxj.nrpc.spring.boot.autoconfigure.config.RpcServerConfig;
import org.example.nrpc.common.model.RpcAddress;
import org.example.nrpc.register.api.RpcRegister;
import org.example.nrpc.server.RpcServer;
import org.example.nrpc.server.util.ServiceManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author 江南小俊
 * @since 2021/7/9
 **/
@Configuration
@EnableConfigurationProperties({RpcServerConfig.class, RpcRegisterConfig.class})
@ConditionalOnExpression("'${nrpc.server.address:false}' ne 'false' && '${nrpc.server.port:false}' ne 'false' " +
                                 "&&'${nrpc.register.connectString:false}' ne 'false' ")
public class RpcServerConfiguration implements ApplicationRunner {
    @Resource
    private RpcServerConfig rpcServerConfig;
    @Resource
    private RpcRegisterConfig rpcRegisterConfig;
    @Resource
    private ApplicationContext context;

    @Bean(initMethod = "init", destroyMethod = "destroy")
    RpcServer rpcServer() {
        RpcServer rpcServer = new RpcServer(rpcServerConfig.getPort());
        return rpcServer;
    }

    @Bean(destroyMethod = "destroy", name = "serverRegister")
    RpcRegister rpcRegister() {
        RpcRegister rpcRegister = ServiceLoaderUtil.loadFirstAvailable(RpcRegister.class);
        rpcRegister.init(rpcRegisterConfig.getConnectString());
        return rpcRegister;
    }

    void init() {
        ServiceManager.setRpcAddress(new RpcAddress(rpcServerConfig.getAddress(), rpcServerConfig.getPort()));
        ServiceManager.setRpcRegister(rpcRegister());

        //扫描@NRpcServiceImpl注解
        Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(NRpcServiceImpl.class);
        beansWithAnnotation.values().forEach(item -> {
            Class<?> apiImplClass = item.getClass();
            Class<?> apiClass;
            NRpcServiceImpl annotation = apiImplClass.getAnnotation(NRpcServiceImpl.class);
            if (Void.class.isAssignableFrom(annotation.value())) {
                Class<?>[] interfaces = apiImplClass.getInterfaces();
                if (interfaces.length > 0) {
                    //父接口
                    apiClass = interfaces[0];
                } else {
                    //取父类
                    apiClass = apiImplClass.getSuperclass();
                }

            } else {
                apiClass = annotation.value();
            }
            ServiceManager.register(apiClass,
                    apiImplClass);
        });
    }

    @Override
    public void run(ApplicationArguments args) {
        init();
        Thread thread = new Thread(rpcServer());
        thread.setName("-rpcServer-");
        thread.setDaemon(true);
        thread.start();
    }
}
