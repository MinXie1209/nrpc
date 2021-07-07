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
    public static final ConcurrentHashMap<Long, CompletableFuture> futureMap = new ConcurrentHashMap<>();

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
        }
    }
}
