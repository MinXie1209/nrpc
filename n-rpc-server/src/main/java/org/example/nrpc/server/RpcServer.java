package org.example.nrpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.nrpc.common.handler.HeartbeatHandler;
import org.example.nrpc.common.protostuff.ProtostuffDecoder;
import org.example.nrpc.common.protostuff.ProtostuffEncoder;
import org.example.nrpc.server.handler.RpcServerHandler;
import org.example.nrpc.server.util.ServiceManager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author 江南小俊
 * @since 2021/7/6
 **/
@Slf4j
@RequiredArgsConstructor()
public class RpcServer implements Runnable {
    private ServerBootstrap serverBootstrap;
    private EventLoopGroup boosGroup;
    private EventLoopGroup workerGroup;
    @NonNull
    private int port;

    /**
     * 初始化
     *
     * @return void
     * @author Jim
     * @since 2021/7/6 下午4:13
     **/
    @PostConstruct
    public void init() {
        boosGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(boosGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
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
                        ch.pipeline().addLast(new RpcServerHandler());
                    }
                });
        log.debug("初始化RpcServer");
    }

    public static void main(String[] args) {
//        ServiceManager.register(OrderService.class, OrderServiceImpl.class);
        RpcServer rpcServer = new RpcServer(8000);
        rpcServer.init();
        Thread thread = new Thread(rpcServer);
        thread.start();
    }

    /**
     * 启动服务
     *
     * @return void
     * @author Jim
     * @since 2021/7/6 下午4:13
     **/

    @Override
    public void run() {
        try {
            ChannelFuture future = serverBootstrap.bind(port).sync();
            log.debug("启动服务:{}", port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("", e);
        }
    }

    /**
     * 释放线程池
     *
     * @return void
     * @author Jim
     * @since 2021/7/6 下午4:13
     **/
    @PreDestroy
    public void destroy() throws InterruptedException {
        boosGroup.shutdownGracefully().sync();
        workerGroup.shutdownGracefully().sync();
        log.debug("释放线程池资源");
    }
}
