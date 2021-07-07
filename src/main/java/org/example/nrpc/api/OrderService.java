package org.example.nrpc.api;

import java.util.concurrent.Future;

/**
 * 订单服务接口
 *
 * @author 江南小俊
 * @since 2021/7/7
 **/
public interface OrderService {
    void getOrderStatus(String orderNo);

    Future<Integer> getOrderStatusAsync(String orderNo);
}
