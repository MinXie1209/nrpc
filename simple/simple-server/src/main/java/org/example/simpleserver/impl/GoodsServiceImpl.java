package org.example.simpleserver.impl;

import org.example.nrpc.simple.api.GoodsService;
import org.example.nrpc.simple.api.OrderService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class GoodsServiceImpl implements GoodsService {

    @Override
    public Future<Integer> getGoodsNum(String goodsId) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        future.complete((int) (Math.random() * 10));
        return future;
    }
}
