package org.example.nrpc.client.proxy.interceptor;

import io.netty.channel.Channel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.example.nrpc.client.RpcClient;
import org.example.nrpc.client.util.ReturnManager;
import org.example.nrpc.common.listener.RpcCompletableFuture;
import org.example.nrpc.common.model.RpcMsg;
import org.example.nrpc.client.proxy.ProxyExecutor;
import org.example.nrpc.common.util.SnowflakeUtil;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * 动态代理处理远程调用
 *
 * @author 江南小俊
 * @since 2021/7/6
 **/
@Slf4j
@RequiredArgsConstructor
public class RpcMethodInterceptor implements MethodInterceptor {
    private ProxyExecutor proxyExecutor = ProxyExecutor.newInstance();
    @NonNull
    private Channel channel;
    /**
     * key: 请求id
     * value: 请求的Future结果 （收到请求结果时操作）
     **/


    /**
     * 统一处理的话
     * 返回类型使用Future(调用get()可以获取返回) 或者 void
     *
     * @param obj
     * @param method
     * @param args
     * @param proxy
     * @return java.lang.Object
     * @author Jim
     * @since 2021/7/6 上午11:43
     **/


    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) {
        Class<?> returnType = method.getReturnType();
        if (Future.class.isAssignableFrom(returnType)) {
            CompletableFuture<Object> completableFuture = new RpcCompletableFuture<>();
            proxyExecutor.execute(() -> {
                handler(method, args, completableFuture);
            });
            return completableFuture;
        } else {
            log.debug("调用的方法返回类型不是{},返回null", Future.class.getName());
            proxyExecutor.execute(() -> {
                long requestId = SnowflakeUtil.snowflakeId();
                handler(method, args, requestId);
            });
            return null;
        }

    }

    /**
     * 只处理远程调用
     *
     * @param method
     * @param args
     * @param requestId
     * @return void
     * @author Jim
     * @since 2021/7/7 上午10:52
     **/
    private void handler(Method method, Object[] args, long requestId) {
        //封装请求
        RpcMsg request = RpcMsg.request(requestId, method, args);
        //发送请求给服务端
        channel.writeAndFlush(request);
        log.debug("发送请求:{}", request);
    }

    /**
     * 处理远程调用，并将返回值保存
     *
     * @param method
     * @param completableFuture
     * @return void
     * @author Jim
     * @since 2021/7/7 上午10:52
     **/

    private void handler(Method method, Object[] args, CompletableFuture<Object> completableFuture) {
        long requestId = SnowflakeUtil.snowflakeId();
        ReturnManager.putFuture(requestId, completableFuture);
        handler(method, args, requestId);
    }
}
