package com.lotime.netty.client;

import com.lotime.netty.handle.PacketHandle;
import com.lotime.netty.packet.LoginRequestPacket;
import com.lotime.netty.packet.LoginResponsePacket;
import com.lotime.netty.packet.Packet;
import com.lotime.netty.util.LoginUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;
import java.util.UUID;

/**
 * @author wangzhimin
 * @version create 2018/9/29 14:55
 */
public class LoginResHandle extends SimpleChannelInboundHandler<LoginResponsePacket>{
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(new Date() + ": 客户端开始登录");

        // 创建登录对象
        LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
        loginRequestPacket.setUserId(UUID.randomUUID().toString());
        loginRequestPacket.setUserName("lotime");
        loginRequestPacket.setPassword("pwd123");

        // 编码
        ByteBuf buffer = PacketHandle.getInstance().encode(ctx.alloc(), loginRequestPacket);

        // 写数据
        ctx.channel().writeAndFlush(buffer);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginResponsePacket msg) throws Exception {
        if (msg.isSuccess()) {
            System.out.println(new Date() + ": 客户端登录成功");
            LoginUtil.markAsLogin(ctx.channel());
        } else {
            System.out.println(new Date() + ": 客户端登录失败，原因：" + msg.getReason());
        }
    }
}
