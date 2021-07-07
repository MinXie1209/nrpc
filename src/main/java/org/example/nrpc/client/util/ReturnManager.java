package org.example.nrpc.client.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 响应结果管理
 *
 * @author 江南小俊
 * @since 2021/7/7
 **/
public class ReturnManager {
    private static final ConcurrentHashMap<Long, CompletableFuture> futureMap = new ConcurrentHashMap<>();

    /**
     * 完成 请求响应的结果
     *
     * @param msgId
     * @param returnObj
     * @return void
     * @author Jim
     * @since 2021/7/7 下午1:59
     **/

    public static void completeReturn(long msgId, Object returnObj) {
        if (futureMap.containsKey(msgId)) {
            futureMap.get(msgId).complete(returnObj);
            futureMap.remove(msgId);
        }
    }

    /**
     * 暂时存放未有结果的Future
     * 待结果完成时移除
     *
     * @param requestId
     * @param completableFuture
     * @return void
     * @author Jim
     * @since 2021/7/7 下午2:22
     **/

    public static void putFuture(long requestId, CompletableFuture<Object> completableFuture) {
        ReturnManager.futureMap.put(requestId, completableFuture);
    }
}
