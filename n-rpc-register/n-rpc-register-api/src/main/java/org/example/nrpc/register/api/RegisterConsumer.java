package org.example.nrpc.register.api;

import java.util.function.Consumer;

/**
 * @author 江南小俊
 * @since 2021/7/8
 **/
public interface RegisterConsumer<T> extends Consumer<T> {
    void cancel(T t);
}
