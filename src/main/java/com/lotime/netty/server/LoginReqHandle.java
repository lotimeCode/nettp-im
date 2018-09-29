package com.lotime.netty.server;

import com.alibaba.fastjson.JSON;
import com.lotime.netty.handle.PacketHandle;
import com.lotime.netty.packet.LoginRequestPacket;
import com.lotime.netty.packet.LoginResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

/**
 * @author wangzhimin
 * @version create 2018/9/29 15:13
 */
public class LoginReqHandle extends SimpleChannelInboundHandler<LoginRequestPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestPacket msg) throws Exception {
        System.out.println(new Date() + ": 客户端开始登录……");

        LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
        loginResponsePacket.setVersion(msg.getVersion());
        if (valid(msg)) {
            loginResponsePacket.setSuccess(true);
            System.out.println(new Date() + ": 登录成功!");
        } else {
            loginResponsePacket.setReason("账号密码校验失败");
            loginResponsePacket.setSuccess(false);
            System.out.println(new Date() + ": 登录失败!");
        }
        // 登录响应
        System.out.println("==== res : " + JSON.toJSONString(loginResponsePacket));
        ByteBuf responseByteBuf = PacketHandle.getInstance().encode(ctx.alloc(), loginResponsePacket);
        ctx.channel().writeAndFlush(responseByteBuf);
    }

    private boolean valid(LoginRequestPacket loginRequestPacket) {
        if(loginRequestPacket.getUserName().equals("lotime") && loginRequestPacket.getPassword().equals("pwd123")){
            return true;
        }else {
            return false;
        }
    }
}
