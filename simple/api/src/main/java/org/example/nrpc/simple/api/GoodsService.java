package org.example.nrpc.simple.api;

import java.util.concurrent.Future;

public interface GoodsService {
    Future<Integer> getGoodsNum(String goodsId);
}
