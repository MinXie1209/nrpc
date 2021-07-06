package org.example.nrpc.proxy.interceptor;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.example.nrpc.proxy.ProxyExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @author 江南小俊
 * @since 2021/7/6
 **/
public class RpcMethodInterceptor implements MethodInterceptor {
    private ProxyExecutor testExecutor = ProxyExecutor.newInstance();

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
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Class<?> returnType = method.getReturnType();
        if (Future.class.isAssignableFrom(returnType)) {
            CompletableFuture<Object> completableFuture = new CompletableFuture<>();
            testExecutor.execute(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler(method, completableFuture);
            });
            return completableFuture;
        } else {
            System.out.println("不返回");
            return null;
        }

    }

    private void handler(Method method, CompletableFuture<Object> completableFuture) {
        switch (method.getName()) {
            case "testString":
                completableFuture.complete("testString");
                break;
            case "testInt":
                completableFuture.complete(12);
                break;
            default:
                completableFuture.complete(method.getName() + " is done");
        }
    }
}
