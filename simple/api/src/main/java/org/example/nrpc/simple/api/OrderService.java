package org.example.nrpc.simple.api;

import com.gitee.jnxj.nrpc.spring.boot.autoconfigure.annotation.NRpcService;
import org.example.nrpc.common.listener.RpcCompletableFuture;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@NRpcService
public interface OrderService {
    Future<Integer> getOrderStatus(String orderNo);

    RpcCompletableFuture<Integer> test(String orderNo);
}
