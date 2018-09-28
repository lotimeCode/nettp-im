package com.lotime.netty.handle;

import com.lotime.netty.common.Command;
import com.lotime.netty.common.SerializerAlgorithm;
import com.lotime.netty.handle.inter.Serializer;
import com.lotime.netty.packet.LoginRequestPacket;
import com.lotime.netty.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangzhimin
 * @version create 2018/9/28 10:07
 *
 *      +----------+----------+----------+-------+----------+-------------
 *      |    魔数  |  版本号  |序列化算法|  指令 | 数据长度 |  数据      |
 *      +----------+----------+----------+-------+----------+-------------
 *      | 4 字节   | 1 字节   |  1 字节  |  1字节|   4字节  |  N字节     |
 *
 * （1）首先，第一个字段是魔数，通常情况下为固定的几个字节（我们这边规定为4个字节）。 为什么需要这个字段，而且还是一个固定的数？假设我们在服务器上开了一个端口，比如 80
 *       端口，如果没有这个魔数，任何数据包传递到服务器，服务器都会根据自定义协议来进行处理，包括不符合自定义协议规范的数据包。例如，我们直接通过 http://服务器ip 来访问服务器
 *      （默认为 80 端口）， 服务端收到的是一个标准的 HTTP 协议数据包，但是它仍然会按照事先约定好的协议来处理 HTTP 协议，显然，这是会解析出错的。而有了这个魔数之后，服务端首
 *      先取出前面四个字节进行比对，能够在第一时间识别出这个数据包并非是遵循自定义协议的，也就是无效数据包，为了安全考虑可以直接关闭连接以节省资源。在 Java 的字节码的二进制文
 *      件中，开头的 8 个字节为0xcafebabe 用来标识这是个字节码文件，亦是异曲同工之妙。
 * （2）接下来一个字节为版本号，通常情况下是预留字段，用于协议升级的时候用到，有点类似 TCP 协议中的一个字段标识是 IPV4 协议还是 IPV6 协议，大多数情况下，这个字段是用不到
 *      的，不过为了协议能够支持升级，我们还是先留着。
 * （3）第三部分，序列化算法表示如何把 Java 对象转换二进制数据以及二进制数据如何转换回 Java 对象，比如 Java 自带的序列化，json，hessian 等序列化方式。
 * （4）第四部分的字段表示指令，关于指令相关的介绍，我们在前面已经讨论过，服务端或者客户端每收到一种指令都会有相应的处理逻辑，这里，我们用一个字节来表示，最高支持128种指
 *      令，对于我们这个 IM 系统来说已经完全足够了。
 * （5）接下来的字段为数据部分的长度，占四个字节。
 * （6）最后一个部分为数据内容，每一种指令对应的数据是不一样的，比如登录的时候需要用户名密码，收消息的时候需要用户标识和具体消息内容等等。
 */
public class PacketHandle {

    private static final int MAGIC_NUMBER = 0x12345678;
    private static final Map<Byte, Class<? extends Packet>> packetTypeMap;
    private static final Map<Byte, Serializer> serializerMap;

    static {
        packetTypeMap = new HashMap<>();
        packetTypeMap.put(Command.LOGIN, LoginRequestPacket.class);

        serializerMap = new HashMap<>();
        serializerMap.put(SerializerAlgorithm.JSON, new JSONSerializer());
    }

    public ByteBuf encode(Packet packet){
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        byte[] bytes = Serializer.DEFAULT.serialize(packet);
        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(packet.version);
        byteBuf.writeByte(Serializer.DEFAULT_SERIALIZER);
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
        return byteBuf;
    }

    public Packet decode(ByteBuf byteBuf){
        //跳过魔数
        byteBuf.readInt();
        //跳过版本
        byteBuf.readByte();
        byte serializeAlgorithm = byteBuf.readByte();
        byte command = byteBuf.readByte();
        int byteLength = byteBuf.readInt();
        byte[] bytes = new byte[byteLength];
        byteBuf.readBytes(bytes);
        Class<? extends Packet> requestType = getRequestType(command);
        Serializer serializer = getSerializer(serializeAlgorithm);
        if (requestType != null && serializer != null) {
            return serializer.deserialize(requestType, bytes);
        }
        return null;
    }

    private Serializer getSerializer(byte serializeAlgorithm) {

        return serializerMap.get(serializeAlgorithm);
    }

    private Class<? extends Packet> getRequestType(byte command) {

        return packetTypeMap.get(command);
    }

}
