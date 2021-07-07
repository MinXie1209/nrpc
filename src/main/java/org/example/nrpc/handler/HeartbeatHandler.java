package org.example.nrpc.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.example.nrpc.model.RpcMsg;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 心跳事件处理
 *
 * @author 江南小俊
 * @since 2021/7/6
 **/
@Slf4j
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
    private AtomicInteger idleNum = new AtomicInteger(0);

    /**
     * 处理心跳事件
     *
     * @param ctx
     * @param evt
     * @return void
     * @author Jim
     * @since 2021/7/6 下午4:30
     **/

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.debug("空闲事件检测：{}", evt);
        super.userEventTriggered(ctx, evt);
        //不是心跳事件
        if (!IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            return;
        }
        IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
        if (idleStateEvent.state().equals(IdleState.READER_IDLE)) {
            //空闲读事件
            int times = idleNum.incrementAndGet();
            if (times >= 3) {
                log.debug("三次空闲读 断开对端连接");
                //三次空闲读 断开客户端连接
                ctx.channel().close();
            } else {
                log.debug("空闲读,发送心跳PING");
                //发送心跳
                ping(ctx);
            }
        }
    }

    /**
     * 处理对端发送的PING-PONG
     * 不是PING_PONG类型，丢给下一个Handler处理 super.channelRead(ctx, msg)
     *
     * @param ctx
     * @param msg
     * @return void
     * @author Jim
     * @since 2021/7/6 下午4:54
     **/

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("接受读事件:{},重置空闲读计数", msg);
        idleNum.set(0);
        if (RpcMsg.class.isAssignableFrom(msg.getClass())) {
            RpcMsg rpcMsg = (RpcMsg) msg;
            if (rpcMsg.getMsgType() == RpcMsg.MsgType.PING) {
                InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
                log.debug("读取到-{}-发起的PING请求", socketAddress.getAddress().getHostAddress());
                //响应
                pong(ctx);
            } else {
                super.channelRead(ctx, msg);
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }

    private void pong(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(RpcMsg.pong());
    }

    private void ping(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(RpcMsg.ping());
    }
}
