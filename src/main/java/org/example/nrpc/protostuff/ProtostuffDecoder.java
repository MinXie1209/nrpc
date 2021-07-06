package org.example.nrpc.protostuff;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 解码器
 * byte[] -> Object
 *
 * @author 江南小俊
 * @since 2021/7/6
 **/
public class ProtostuffDecoder extends LengthFieldBasedFrameDecoder {
    /**
     * @param maxFrameLength      帧的最大长度。 如果帧的长度大于此值，则会抛出TooLongFrameException 。
     * @param lengthFieldOffset   长度字段的偏移量
     * @param lengthFieldLength   长度字段的长度
     * @param lengthAdjustment    添加到长度字段值的补偿值
     * @param initialBytesToStrip 从解码帧中剥离的第一个字节数
     * @return
     * @author Jim
     * @since 2021/7/6 下午4:22
     **/

    public ProtostuffDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment,
                             int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf byteBuf = (ByteBuf) super.decode(ctx, in);
        if (byteBuf == null) return null;
        byte[] dst = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(dst);
        return ProtostuffUtil.deserialize(dst);
    }
}
