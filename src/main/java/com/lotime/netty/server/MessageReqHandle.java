package com.lotime.netty.server;

import com.lotime.netty.handle.PacketHandle;
import com.lotime.netty.packet.MessageRequestPacket;
import com.lotime.netty.packet.MessageResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

/**
 * @author wangzhimin
 * @version create 2018/9/29 15:15
 */
public class MessageReqHandle extends SimpleChannelInboundHandler<MessageRequestPacket>{
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageRequestPacket msg) throws Exception {
        System.out.println("服务端接收到信息 ...");
        System.out.println(msg.getTimestamp() + " : " + msg.getMessage());
        MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
        messageResponsePacket.setMessage("你好 。。。");
        messageResponsePacket.setTimestamp(new Date());
        ByteBuf responseByteBuf = PacketHandle.getInstance().encode(ctx.alloc(), messageResponsePacket);
        ctx.channel().writeAndFlush(responseByteBuf);
    }
}
