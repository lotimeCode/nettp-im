package Demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author wangzhimin
 * @version create 2018/9/27 14:54
 */
public class NettyClient {
    public static void main(String[] args) {
        try {
            startNettyClient("127.0.0.1",8000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 启动netty客户端
     * @throws InterruptedException
     */
    public static void startNettyClient(String host,int port) throws InterruptedException {
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        //给客户端 Channel绑定自定义属性
        bootstrap.attr(AttributeKey.newInstance("bootstrap"),"nettyClient")
                //表示连接的超时时间，超过这个时间还是建立不上的话则代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                //表示是否开启 TCP 底层心跳机制，true 为开启
                .option(ChannelOption.SO_KEEPALIVE,true)
                //表示是否开始 Nagle 算法，true 表示关闭，false 表示开启，通俗地说，如果要求高实时性，有数据发送时就马上发送，就设置为 true 关闭，如果需要减少发送次数减少网络交互，就设置为 false 开启
                .option(ChannelOption.TCP_NODELAY,true);
        // 1.指定线程模型
        bootstrap.group(workGroup)
                // 2.指定 IO 类型为 NIO
                .channel(NioSocketChannel.class)
                // 3.IO 处理逻辑
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                });
        int retry = 0;
        Channel channel = bootstrap.connect(host,port).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if(future.isSuccess()){
                    System.out.println(String.format("connect [%s:%d] success",host,port));
                }else {
                    System.out.println(String.format("connect [%s:%d] fail",host,port));
                    connect(bootstrap, host, port, retry);
                }
            }
        }).channel();

        try {
            sendMessage(channel, "nice to meet you");
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * 失败重连,指数退避
     * @param bootstrap
     * @param host
     * @param port
     */
    public static void connect(Bootstrap bootstrap,String host,int port,final int retryNum){
        int order = retryNum + 1;
        if(order > 5){
            System.out.println("can't connect host");
            return;
        }
        System.out.println(String.format("number %d time connect",order));
        bootstrap.bind(host,port).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if(future.isSuccess()){
                    System.out.println(String.format("connect [%s:%d] success",host,port));
                    return;
                }else{
                    System.out.println(String.format("connect [%s:%d] fail",host,port));
                    bootstrap.config().group().schedule(() -> connect(bootstrap,host,port,order),1 << order,
                            TimeUnit.SECONDS);
                }
            }
        });
    }

    /**
     * 发送信息
     * @param channel
     * @param mes
     * @throws Exception
     */
    public static void sendMessage(Channel channel, String mes) throws Exception{
        int i = 0;
        while (true) {
            channel.writeAndFlush(new Date() + mes);
            System.out.println("send message " + i + " times");
            Thread.sleep(2000);
            if(++i > 100){
                break;
            }
        }
    }
}
