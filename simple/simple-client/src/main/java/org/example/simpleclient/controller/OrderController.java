package org.example.simpleclient.controller;

import org.example.nrpc.client.proxy.BeanFactory;
import org.example.nrpc.simple.api.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @author 江南小俊
 * @since 2021/7/7
 **/
@RestController
@RequestMapping("/api/order")
public class OrderController {
    @GetMapping("/{id}")
    public Integer get(@PathVariable("id") String id) throws ExecutionException, InterruptedException {
        return BeanFactory.getBean(OrderService.class).getOrderStatus(id).get();
    }
}
