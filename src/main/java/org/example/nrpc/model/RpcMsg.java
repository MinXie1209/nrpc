package org.example.nrpc.model;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * RPC调用基础信息
 *
 * @author 江南小俊
 * @since 2021/7/6
 **/
@Data
public class RpcMsg {
    private int msgType = MsgType.REQUEST;
    private long msgId;

    private String className;
    private String methodName;
    private Object[] args;
    private Class<?>[] parameterTypes;

    private Object returnObj;

    public static class MsgType {
        public static final int REQUEST = 1;
        public static final int RESPONSE = 2;
        public static final int PING = -1;
        public static final int PONG = -2;
    }

    public static RpcMsg ping() {
        RpcMsg rpcMsg = new RpcMsg();
        rpcMsg.setMsgType(MsgType.PING);
        return rpcMsg;
    }

    public static RpcMsg pong() {
        RpcMsg rpcMsg = new RpcMsg();
        rpcMsg.setMsgType(MsgType.PONG);
        return rpcMsg;
    }

    public static RpcMsg request(long requestId, Method method, Object[] args) {
        RpcMsg rpcMsg = new RpcMsg();
        rpcMsg.setMsgType(MsgType.REQUEST);
        rpcMsg.setMsgId(requestId);
        rpcMsg.setClassName(method.getDeclaringClass().getName());
        rpcMsg.setMethodName(method.getName());
        rpcMsg.setParameterTypes(method.getParameterTypes());
        rpcMsg.setArgs(args);
        return rpcMsg;
    }

    public static RpcMsg response(long msgId, Object returnObj) {
        RpcMsg rpcMsg = new RpcMsg();
        rpcMsg.setMsgType(MsgType.RESPONSE);
        rpcMsg.setMsgId(msgId);
        rpcMsg.setReturnObj(returnObj);
        return rpcMsg;
    }
}
