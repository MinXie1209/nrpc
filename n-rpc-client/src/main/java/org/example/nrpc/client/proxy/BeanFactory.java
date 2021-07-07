package org.example.nrpc.client.proxy;

import lombok.Data;
import net.sf.cglib.proxy.Enhancer;
import org.example.nrpc.client.RpcClient;
import org.example.nrpc.client.proxy.interceptor.RpcMethodInterceptor;

/**
 * @author 江南小俊
 * @since 2021/7/6
 **/
@Data
public class BeanFactory {
    private static Enhancer enhancer = new Enhancer();
    private static RpcClient rpcClient;

    public static void setRpcClient(RpcClient rpcClient) {
        BeanFactory.rpcClient = rpcClient;
    }

    public static <T> T getBean(Class<T> tClass) {
        enhancer.setSuperclass(tClass);
        enhancer.setCallback(new RpcMethodInterceptor(rpcClient));
        return (T) enhancer.create();
    }
}
