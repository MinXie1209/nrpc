package org.example.nrpc.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.example.nrpc.model.RpcMsg;
import org.example.nrpc.server.util.ServiceManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
        log.debug("收到请求:{}", msg);
        if (msg.getMsgType() == RpcMsg.MsgType.REQUEST) {
            Object returnObj = invoke(msg);
            response(ctx, returnObj, msg.getMsgId());
        }
    }

    private void response(ChannelHandlerContext ctx, Object returnObj, long msgId) {
        if (returnObj == null || !Future.class.isAssignableFrom(returnObj.getClass())) {
            ctx.writeAndFlush(RpcMsg.response(msgId, returnObj));
        } else {
            try {
                ctx.writeAndFlush(RpcMsg.response(msgId, ((Future) returnObj).get()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 反射调用
     *
     * @param msg
     * @return Object
     * @author Jim
     * @since 2021/7/7 上午11:40
     **/

    private Object invoke(RpcMsg msg) {
        try {
            Class<?> aClass = Class.forName(ServiceManager.getClassImpl(msg.getClassName()));
            Object obj = aClass.newInstance();
            //找实现类
            Method method = aClass.getMethod(msg.getMethodName(), msg.getParameterTypes());
            return method.invoke(obj, msg.getArgs());
        } catch (ClassNotFoundException e) {
            log.error("找不到类：{}", msg.getClassName());
            return e;
        } catch (NoSuchMethodException e) {
            log.error("找不到方法:{}", msg.getMethodName());
            return e;
        } catch (IllegalAccessException e) {
            log.error("", e);
            return e;
        } catch (InstantiationException e) {
            log.error("", e);
            return e;
        } catch (InvocationTargetException e) {
            log.error("", e);
            return e;
        } catch (Exception e) {
            log.error("", e);
            return e;
        }
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
