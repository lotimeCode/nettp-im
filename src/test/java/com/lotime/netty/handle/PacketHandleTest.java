package com.lotime.netty.handle;

import com.lotime.netty.handle.inter.Serializer;
import com.lotime.netty.packet.LoginRequestPacket;
import com.lotime.netty.packet.Packet;
import io.netty.buffer.ByteBuf;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author wangzhimin
 * @version create 2018/9/28 10:58
 */
public class PacketHandleTest {
    @Test
    public void testEncode(){
        LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
        loginRequestPacket.setUserId(123);
        loginRequestPacket.setUserName("lotime");
        loginRequestPacket.setPassword("lotime1234567");
        PacketHandle packetHandle = new PacketHandle();
        ByteBuf byteBuf = packetHandle.encode(loginRequestPacket);
        Packet packet = packetHandle.decode(byteBuf);

        Serializer serializer = new JSONSerializer();
        Assert.assertArrayEquals(serializer.serialize(loginRequestPacket), serializer.serialize(packet));
    }
}
