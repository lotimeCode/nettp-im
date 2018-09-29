package com.lotime.netty.client;

import com.lotime.netty.packet.MessageResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author wangzhimin
 * @version create 2018/9/29 15:04
 */
public class MessageResHandle extends SimpleChannelInboundHandler<MessageResponsePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageResponsePacket msg) throws Exception {
        System.out.println("客户端收到信息 ...");
        System.out.println(msg.getTimestamp() + " : " + msg.getMessage());
    }
}
