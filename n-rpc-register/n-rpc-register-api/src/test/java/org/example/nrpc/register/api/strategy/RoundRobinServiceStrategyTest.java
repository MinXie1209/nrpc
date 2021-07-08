package org.example.nrpc.register.api.strategy;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 江南小俊
 * @since 2021/7/8
 **/
class RoundRobinServiceStrategyTest {
    @Test
    public void it() {
        AtomicInteger atomicInteger = new AtomicInteger(Integer.MAX_VALUE);
        System.out.println(atomicInteger.get());
        System.out.println(atomicInteger.incrementAndGet());
        System.out.println(atomicInteger.incrementAndGet());
    }
}