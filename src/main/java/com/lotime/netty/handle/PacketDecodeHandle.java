package com.lotime.netty.handle;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author wangzhimin
 * @version create 2018/9/29 14:32
 */
public class PacketDecodeHandle extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("PacketDecodeHandle come in ...");
        out.add(PacketHandle.getInstance().decode(in));
    }
}
