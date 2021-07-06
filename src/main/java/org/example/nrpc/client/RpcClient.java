package org.example.nrpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.nrpc.client.handler.RpcClientHandler;
import org.example.nrpc.client.listener.ConnectFutureListener;
import org.example.nrpc.protostuff.ProtostuffDecoder;
import org.example.nrpc.protostuff.ProtostuffEncoder;
import org.example.nrpc.handler.HeartbeatHandler;

import javax.annotation.PostConstruct;

/**
 * Rpc客户端
 *
 * @author 江南小俊
 * @since 2021/7/6
 **/
@Slf4j
@Data
@RequiredArgsConstructor
public class RpcClient implements Runnable {
    private EventLoopGroup group;
    private Bootstrap bootstrap;
    @NonNull
    private String inetHost;
    @NonNull
    private int inetPort;

    @PostConstruct
    public void init() {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //编码器 Object->byte[]
                        ch.pipeline().addLast("encoder", new ProtostuffEncoder());
                        //解码器 byte[] -> Object
                        ch.pipeline().addLast("decoder", new ProtostuffDecoder(1024 * 1024, 0, 4, 0, 4));
                        //心跳检测 60s没有读操作触发事件IdleStateEvent
                        ch.pipeline().addLast(new IdleStateHandler(60, 0, 0));
                        //心跳处理
                        ch.pipeline().addLast(new HeartbeatHandler());
                        //rpc请求处理
                        ch.pipeline().addLast(new RpcClientHandler());
                    }
                });

    }

    @Override
    public void run() {
        ChannelFuture channelFuture = bootstrap.connect(inetHost, inetPort);
        channelFuture.addListener(new ConnectFutureListener());
    }
}
