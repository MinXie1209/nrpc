package org.example.nrpc.simple.api;

import java.util.concurrent.Future;

public interface OrderService {
    Future<Integer> getOrderStatus(String orderNo);
}
