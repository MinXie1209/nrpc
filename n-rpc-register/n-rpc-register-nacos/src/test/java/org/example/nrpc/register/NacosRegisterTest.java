package org.example.nrpc.register;

import org.example.nrpc.register.api.RegisterConsumer;
import org.example.nrpc.register.api.model.RpcServiceInstance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 江南小俊
 * @since 2021/7/8
 **/
class NacosRegisterTest {

    @Test
    void init() {
//        try {
//            NacosRegister nacosRegister = new NacosRegister();
//            nacosRegister.init("127.0.0.1:8848");
//            nacosRegister.addListener("org.example.nrpc.simple.api.GoodsService", (list) -> {
//            });
//            nacosRegister.addListener("org.example.nrpc.simple.api.OrderService", (list) -> {
//            });
//            nacosRegister.register(RpcServiceInstance.builder()
//                    .serviceName("org.example.nrpc.simple.api.GoodsService")
//                    .address("127.0.0.1")
//                    .port(9000)
//                    .build());
//            nacosRegister.register(RpcServiceInstance.builder()
//                    .serviceName("org.example.nrpc.simple.api.OrderService")
//                    .address("127.0.0.1")
//                    .port(9000)
//                    .build());
//
//
//            NacosRegister nacosRegister2 = new NacosRegister();
//            nacosRegister2.init("127.0.0.1:8848");
//            nacosRegister2.register(RpcServiceInstance.builder()
//                    .serviceName("org.example.nrpc.simple.api.GoodsService")
//                    .address("127.0.0.1")
//                    .port(9001)
//                    .build());
//            nacosRegister2.register(RpcServiceInstance.builder()
//                    .serviceName("org.example.nrpc.simple.api.OrderService")
//                    .address("127.0.0.1")
//                    .port(9001)
//                    .build());
//            Thread.sleep(5 * 60000);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}