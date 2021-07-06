package org.example.nrpc.proxy;

import net.sf.cglib.proxy.Enhancer;
import org.example.nrpc.proxy.interceptor.RpcMethodInterceptor;

/**
 * @author 江南小俊
 * @since 2021/7/6
 **/
public class BeanFactory {
    private static Enhancer enhancer = new Enhancer();

    public static <T> T getBean(Class<T> tClass) {
        enhancer.setSuperclass(tClass);
        enhancer.setCallback(new RpcMethodInterceptor());
        return (T) enhancer.create();
    }
}
