package org.example.nrpc.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.nrpc.client.RpcClient;
import org.example.nrpc.client.proxy.BeanFactory;
import org.example.nrpc.client.util.ReturnManager;
import org.example.nrpc.common.model.RpcMsg;

import java.util.concurrent.TimeUnit;

/**
 * RPC请求处理
 *
 * @author 江南小俊
 * @since 2021/7/6
 **/
@Slf4j
@RequiredArgsConstructor
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcMsg> {
    @NonNull
    private RpcClient rpcClient;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMsg msg) {
        if (msg.getMsgType() == RpcMsg.MsgType.RESPONSE) {
            ReturnManager.completeReturn(msg.getMsgId(), msg.getReturnObj());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //分析一下这里，这里应该是服务端连接不上了，zk可能没有变化的
        super.channelInactive(ctx);
        ctx.channel().close();
        log.debug("服务端连接不上了,1s后开始重新连接");
        ctx.channel().eventLoop().schedule(() -> BeanFactory.channelInactive(ctx.channel()), 1, TimeUnit.SECONDS);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.error("", cause);
    }
}