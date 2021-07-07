package org.example.nrpc.client.listener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.nrpc.client.RpcClient;

import java.util.concurrent.TimeUnit;

/**
 * 连接监听
 *
 * @author 江南小俊
 * @since 2021/7/6
 **/
@Slf4j
@RequiredArgsConstructor
public class ConnectFutureListener implements ChannelFutureListener {
    @NonNull
    private RpcClient rpcClient;

    @Override
    public void operationComplete(ChannelFuture future) {
        if (future.isSuccess()) {
            log.debug("连接成功");
            rpcClient.setChannel(future.channel());
        } else {
            log.debug("连接失败，1s后重新建立连接");
            future.channel().eventLoop().schedule(rpcClient, 1, TimeUnit.SECONDS);
        }
    }
}
