package com.lotime.netty.server;

import com.alibaba.fastjson.JSON;
import com.lotime.netty.handle.PacketHandle;
import com.lotime.netty.packet.*;
import com.lotime.netty.util.LoginUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * @author wangzhimin
 * @version create 2018/9/28 19:19
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        ByteBuf requestByteBuf = (ByteBuf) msg;

        Packet packet = PacketHandle.getInstance().decode(requestByteBuf);

        if (packet instanceof LoginRequestPacket) {
            System.out.println(new Date() + ": 客户端开始登录……");
            // 登录流程
            LoginRequestPacket loginRequestPacket = (LoginRequestPacket) packet;

            LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
            loginResponsePacket.setVersion(packet.getVersion());
            if (valid(loginRequestPacket)) {
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
        }else if(packet instanceof MessageRequestPacket){
            MessageRequestPacket messageRequestPacket = (MessageRequestPacket) packet;
            System.out.println("服务端接收到信息");
            System.out.println(messageRequestPacket.getTimestamp() + " : " + messageRequestPacket.getMessage());
            MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
            messageResponsePacket.setMessage("你好 。。。");
            messageResponsePacket.setTimestamp(new Date());
            ByteBuf responseByteBuf = PacketHandle.getInstance().encode(ctx.alloc(), messageResponsePacket);
            ctx.channel().writeAndFlush(responseByteBuf);
        }
    }

    private boolean valid(LoginRequestPacket loginRequestPacket) {
        if(loginRequestPacket.getUserName().equals("lotime") && loginRequestPacket.getPassword().equals("pwd123")){
            return true;
        }else {
            return false;
        }
    }
}
