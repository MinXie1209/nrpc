package org.example.simpleserver.impl;

import org.example.nrpc.simple.api.OrderService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class OrderServiceImpl implements OrderService {
    @Override
    public Future<Integer> getOrderStatus(String orderNo) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        future.complete((int) (Math.random() * 10));
        return future;
    }
}
