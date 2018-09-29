package com.lotime.netty.handle;

import com.lotime.netty.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author wangzhimin
 * @version create 2018/9/29 14:28
 */
public class PacketEncodeHandle extends MessageToByteEncoder<Packet> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
        out = PacketHandle.getInstance().encode(ctx.alloc(),msg);
    }
}
