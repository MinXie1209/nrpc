package org.example.nrpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
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
import org.example.nrpc.client.proxy.BeanFactory;
import org.example.nrpc.common.model.RpcAddress;
import org.example.nrpc.common.protostuff.ProtostuffDecoder;
import org.example.nrpc.common.protostuff.ProtostuffEncoder;
import org.example.nrpc.common.handler.HeartbeatHandler;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Rpc客户端
 *
 * @author 江南小俊
 * @since 2021/7/6
 **/
@Slf4j
@Data
@RequiredArgsConstructor
public class RpcClient {
    private EventLoopGroup group;
    private Bootstrap bootstrap;

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
                        ch.pipeline().addLast(new IdleStateHandler(65, 0, 0));
                        //心跳处理
                        ch.pipeline().addLast(new HeartbeatHandler());
                        //rpc请求处理
                        ch.pipeline().addLast(new RpcClientHandler(RpcClient.this));
                    }
                });

    }

    public void run(Consumer<Channel> consumer, RpcAddress rpcAddress) {
        ChannelFuture channelFuture = bootstrap.connect(rpcAddress.getHost(), rpcAddress.getPort());
        channelFuture.addListener(new ConnectFutureListener(consumer, this, rpcAddress));
    }

    @PreDestroy
    public void destroy() {
        log.debug("线程池连接资源释放");
        try {
            group.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("", e);
        }
    }
}
