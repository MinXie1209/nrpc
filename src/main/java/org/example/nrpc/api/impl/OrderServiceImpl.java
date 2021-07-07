package org.example.nrpc.api.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.nrpc.api.OrderService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * 服务实现
 *
 * @author 江南小俊
 * @since 2021/7/7
 **/
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Override
    public void getOrderStatus(String orderNo) {
        log.debug("获取订单-{}-状态", orderNo);
    }

    @Override
    public Future<Integer> getOrderStatusAsync(String orderNo) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        future.complete(1);
        return future;
    }
}
