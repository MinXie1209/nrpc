package org.example.nrpc.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.example.nrpc.model.RpcMsg;

/**
 * RPC响应处理
 *
 * @author 江南小俊
 * @since 2021/7/6
 **/
@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMsg msg) {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.debug("客户端连接断开");
        ctx.channel().close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.debug("接受到客户端连接");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
