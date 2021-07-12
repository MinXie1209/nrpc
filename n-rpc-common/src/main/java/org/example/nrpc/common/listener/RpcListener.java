package org.example.nrpc.common.listener;

/**
 * @author 江南小俊
 * @since 2021/7/13
 */
public interface RpcListener {
    void operationComplete(RpcCompletableFuture rpcCompletableFuture);
}
