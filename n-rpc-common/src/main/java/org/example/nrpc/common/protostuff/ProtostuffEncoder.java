package org.example.nrpc.common.protostuff;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器
 * Object -> byte[]
 *
 * @author 江南小俊
 * @since 2021/7/6
 **/
public class ProtostuffEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] serialize = ProtostuffUtil.serialize(msg);
        out.writeInt(serialize.length);
        out.writeBytes(serialize);
    }
}
