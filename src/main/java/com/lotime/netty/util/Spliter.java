package com.lotime.netty.util;

import com.lotime.netty.handle.PacketHandle;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author wangzhimin
 * @version create 2018/10/11 16:25
 *
 * 我们需要知道，尽管我们在应用层面使用了 Netty，但是对于操作系统来说，只认 TCP 协议，尽管我们的应用层是按照 ByteBuf 为
 * 单位来发送数据，但是到了底层操作系统仍然是按照字节流发送数据，因此，数据到了服务端，也是按照字节流的方式读入，然后到
 * 了 Netty 应用层面，重新拼装成 ByteBuf，而这里的 ByteBuf 与客户端按顺序发送的 ByteBuf 可能是不对等的。因此，我们需要在
 * 客户端根据自定义协议来组装我们应用层的数据包，然后在服务端根据我们的应用层的协议来组装数据包，这个过程通常在服务端称
 * 为拆包，而在客户端称为粘包。拆包和粘包是相对的，一端粘了包，另外一端就需要将粘过的包拆开，举个栗子，发送端将三个数据
 * 包粘成两个 TCP 数据包发送到接收端，接收端就需要根据应用协议将两个数据包重新组装成三个数据包。
 *
 * Netty 自带的拆包器
    1. 固定长度的拆包器 FixedLengthFrameDecoder
    如果你的应用层协议非常简单，每个数据包的长度都是固定的，比如 100，那么只需要把这个拆包器加到 pipeline 中，Netty 会把
    一个个长度为 100 的数据包 (ByteBuf) 传递到下一个 channelHandler。

    2. 行拆包器 LineBasedFrameDecoder
    从字面意思来看，发送端发送数据包的时候，每个数据包之间以换行符作为分隔，接收端通过 LineBasedFrameDecoder 将粘过的
    ByteBuf 拆分成一个个完整的应用层数据包。

    3. 分隔符拆包器 DelimiterBasedFrameDecoder
    DelimiterBasedFrameDecoder 是行拆包器的通用版本，只不过我们可以自定义分隔符。

    4. 基于长度域拆包器 LengthFieldBasedFrameDecoder
    最后一种拆包器是最通用的一种拆包器，只要你的自定义协议中包含长度域字段，均可以使用这个拆包器来实现应用层拆包。由于
    上面三种拆包器比较简单，读者可以自行写出 demo，接下来，我们就结合我们小册的自定义协议，来学习一下如何使用基于长度域
    的拆包器来拆解我们的数据包。
 */
public class Spliter extends LengthFieldBasedFrameDecoder {
    private static final int LENGTH_FIELD_OFFSET = 7;
    private static final int LENGTH_FIELD_LENGTH = 4;
    public Spliter() {
        super(Integer.MAX_VALUE, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        //通过检查模式，屏蔽非本协议客户端
        if (in.getInt(in.readerIndex()) != PacketHandle.MAGIC_NUMBER) {
            ctx.channel().close();
            System.out.println("非本协议数据，关闭连接");
            return null;
        }
        return super.decode(ctx, in);
    }
}
