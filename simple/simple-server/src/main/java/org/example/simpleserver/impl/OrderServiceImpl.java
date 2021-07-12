package org.example.simpleserver.impl;

import com.gitee.jnxj.nrpc.spring.boot.autoconfigure.annotation.NRpcServiceImpl;
import org.example.nrpc.common.listener.RpcCompletableFuture;
import org.example.nrpc.simple.api.OrderService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@NRpcServiceImpl()
public class OrderServiceImpl implements OrderService {
    @Override
    public Future<Integer> getOrderStatus(String orderNo) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        future.complete((int) (Math.random() * 10));
        return future;
    }

    @Override
    public RpcCompletableFuture<Integer> test(String orderNo) {
        RpcCompletableFuture<Integer> future = new RpcCompletableFuture<>();
        future.complete((int) (Math.random() * 10));
        return future;
    }
}
