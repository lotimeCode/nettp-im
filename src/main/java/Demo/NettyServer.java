package Demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.nio.charset.Charset;

/**
 * @author wangzhimin
 * @version create 2018/9/27 14:22
 *
 * (1)首先看到，我们创建了两个NioEventLoopGroup，这两个对象可以看做是传统IO编程模型的两大线程组，bossGroup表示监听端口，
 * accept 新连接的线程组，workerGroup表示处理每一条连接的数据读写的线程组，不理解的同学可以看一下上一小节《Netty是什么》。
 * 用生活中的例子来讲就是，一个工厂要运作，必然要有一个老板负责从外面接活，然后有很多员工，负责具体干活，老板就是bossGroup，
 * 员工们就是workerGroup，bossGroup接收完连接，扔给workerGroup去处理。
 * (2)接下来 我们创建了一个引导类 ServerBootstrap，这个类将引导我们进行服务端的启动工作，直接new出来开搞。
 * 我们通过.group(bossGroup, workerGroup)给引导类配置两大线程组，这个引导类的线程模型也就定型了。
 * (3)然后，我们指定我们服务端的 IO 模型为NIO，我们通过.channel(NioServerSocketChannel.class)来指定 IO 模型，当然，这里也有其他的选择，如果你想指定 IO 模型为
 * BIO，那么这里配置上OioServerSocketChannel.class类型即可，当然通常我们也不会这么做，因为Netty的优势就在于NIO。
 * (4)接着，我们调用childHandler()方法，给这个引导类创建一个ChannelInitializer，这里主要就是定义后续每条连接的数据读写，
 * 业务处理逻辑，不理解没关系，在后面我们会详细分析。ChannelInitializer这个类中，我们注意到有一个泛型参数NioSocketChannel，
 * 这个类呢，就是 Netty 对 NIO 类型的连接的抽象，而我们前面NioServerSocketChannel也是对 NIO 类型的连接的抽象，
 * NioServerSocketChannel和NioSocketChannel的概念可以和 BIO 编程模型中的ServerSocket以及Socket两个概念对应上

https://juejin.im
掘金 — 一个帮助开发者成长的社区
 */
public class NettyServer {
    public static void main(String[] args) {
        startNettyServer(8000);
    }


    /**
     * 启动netty服务端
     * @param port
     */
    public static void startNettyServer(int port){
        //表示监听端口，accept 新连接的线程组
        NioEventLoopGroup boss = new NioEventLoopGroup();
        //表示处理每一条连接的数据读写的线程组
        NioEventLoopGroup worker = new NioEventLoopGroup();
        //netty server 引导类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        //handler()用于指定在服务端启动过程中的一些逻辑
        serverBootstrap.handler(new ChannelInitializer<NioServerSocketChannel>() {
            @Override
            protected void initChannel(NioServerSocketChannel nioServerSocketChannel) throws Exception {
                System.out.println("==========>>>netty server start ...<<<==========");
            }
        });
        //attr()方法可以给服务端的 channel，也就是NioServerSocketChannel指定一些自定义属性，然后我们可以通过channel.attr()取出这个属性
        serverBootstrap.attr(AttributeKey.newInstance("serverName"),"nettyServer");
        //给每一条连接指定自定义属性
        serverBootstrap.childAttr(AttributeKey.newInstance("serverBootstrap"),"serverBootstrap");
        //表示是否开启TCP底层心跳机制，true为开启
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
        //表示是否开始Nagle算法，true表示关闭，false表示开启，通俗地说，如果要求高实时性，有数据发送时就马上发送，就关闭，如果需要减少发送次数减少网络交互，就开启
        serverBootstrap.childOption(ChannelOption.TCP_NODELAY,true);
        //表示系统用于临时存放已完成三次握手的请求的队列的最大长度，如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
        serverBootstrap.option(ChannelOption.SO_BACKLOG,1024);
        serverBootstrap.group(boss,worker)
                //指定服务端的 IO 模型为NIO
                .channel(NioServerSocketChannel.class)
                //定义后续每条连接的数据读写，业务处理逻辑,childHandler()用于指定处理新连接数据的读写处理逻辑
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new ServerHandle());
                    }
                }).bind(port).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if(future.isSuccess()){
                    System.out.println(String.format("bind port [ %d ] success", port));
                }else{
                    System.out.println(String.format("bind port [ %d ] fail", port));
                    //若绑定失败，则继续试探绑定port+1 端口
                    bindPort(serverBootstrap,port + 1);
                }
            }
        });
    }

    /**
     * 若绑定失败，则继续试探绑定port+1 端口
     * @param serverBootstrap
     * @param port
     */
    public static void bindPort(ServerBootstrap serverBootstrap,int port){
        serverBootstrap.bind(port).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if(future.isSuccess()){
                    System.out.println(String.format("bind port [ %d ] success", port));
                    return;
                }else{
                    System.out.println(String.format("bind port [ %d ] fail", port));
                    bindPort(serverBootstrap,port + 1);
                }
            }
        });
    }
}


class ServerHandle extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("server receive message : " + byteBuf.toString(Charset.forName("UTF-8")));
        ctx.channel().writeAndFlush(getByteBuf(ctx));
    }


    private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
        byte[] bytes = "你好，欢迎关注我的微信公众号，《闪电侠的博客》!".getBytes(Charset.forName("utf-8"));

        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeBytes(bytes);
        return buffer;
    }
}
