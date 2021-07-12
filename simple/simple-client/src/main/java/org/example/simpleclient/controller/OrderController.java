package org.example.simpleclient.controller;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class OrderController {
    @GetMapping("/{id}")
    public Integer get(@PathVariable("id") String id) throws ExecutionException, InterruptedException {
        return BeanFactory.getBean(OrderService.class).getOrderStatus(id).get();
    }

    @GetMapping("/test/{id}")
    public void test(@PathVariable("id") String id) throws ExecutionException, InterruptedException {
        BeanFactory.getBean(OrderService.class).test(id).addListener((future) -> {
            if (future.isDone()) {
                try {
                    log.debug("操作完成：{}", future.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } else {
                log.debug("操作失败");
            }
        });
    }
}
