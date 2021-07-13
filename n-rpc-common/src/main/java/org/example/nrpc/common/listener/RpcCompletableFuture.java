package org.example.nrpc.common.listener;

import lombok.Data;

import java.util.concurrent.CompletableFuture;

/**
 * @author 江南小俊
 * @since 2021/7/13
 */
@Data
public class RpcCompletableFuture<T> extends CompletableFuture<T> {
    private RpcListener rpcListener;

    public RpcCompletableFuture addListener(RpcListener rpcListener) {
        this.rpcListener = rpcListener;
        return this;
    }

    @Override
    public boolean complete(T value) {
        boolean complete = super.complete(value);
        if (rpcListener != null) {
            rpcListener.operationComplete(this);
        }
        return complete;
    }

    public static <U> RpcCompletableFuture<U> completedFuture(U value) {
        RpcCompletableFuture<U> objectRpcCompletableFuture = new RpcCompletableFuture<>();
        objectRpcCompletableFuture.complete(value);
        return objectRpcCompletableFuture;
    }
}
