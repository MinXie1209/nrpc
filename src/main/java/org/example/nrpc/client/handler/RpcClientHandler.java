package org.example.nrpc.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import org.example.nrpc.model.RpcMsg;

/**
 * RPC请求处理
 *
 * @author 江南小俊
 * @since 2021/7/6
 **/
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMsg msg) {

    }
}