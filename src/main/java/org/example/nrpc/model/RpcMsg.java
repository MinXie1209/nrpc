package org.example.nrpc.model;

import lombok.Data;

/**
 * RPC调用基础信息
 *
 * @author 江南小俊
 * @since 2021/7/6
 **/
@Data
public class RpcMsg {
    private int msgType = MsgType.REQUEST;

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
}
