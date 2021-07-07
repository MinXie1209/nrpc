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
import org.example.nrpc.common.protostuff.ProtostuffDecoder;
import org.example.nrpc.common.protostuff.ProtostuffEncoder;
import org.example.nrpc.common.handler.HeartbeatHandler;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

    private Channel channel;
    private ChannelFutureListener channelFutureListener = new ConnectFutureListener(this);

    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient("127.0.0.1", 8000);
        rpcClient.init();
        Thread thread = new Thread(rpcClient);
        thread.setDaemon(false);
        thread.start();
        try {
            Thread.sleep(5000);
            BeanFactory.setRpcClient(rpcClient);
//            OrderService orderService = BeanFactory.getBean(OrderService.class);
//            orderService.getOrderStatus("OD91323612369213");
//            String orderNo = "OD123721037021";
//            Future<Integer> orderStatus = orderService.getOrderStatusAsync(orderNo);
//            log.info("订单-{}-状态：{}", orderNo, orderStatus.get());
        } catch (InterruptedException e) {
            e.printStackTrace();}
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }

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

    @Override
    public void run() {
        ChannelFuture channelFuture = bootstrap.connect(inetHost, inetPort);
        channelFuture.addListener(channelFutureListener);
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
