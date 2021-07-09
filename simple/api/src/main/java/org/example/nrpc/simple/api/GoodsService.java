package org.example.nrpc.simple.api;

import com.gitee.jnxj.nrpc.spring.boot.autoconfigure.annotation.NRpcService;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@NRpcService
public interface GoodsService {
    Future<Integer> getGoodsNum(String goodsId);
}
