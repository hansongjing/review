package com.hsj.netty.demo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by hanhansongjiang on 17/6/15.
 */
public class SimpleClient {

    private String host;

    private int port;


    private FixedChannelPool pool;


    protected GenericObjectPool.Config poolConfig;
    protected PoolableObjectFactory factory;

    public SimpleClient(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public void request(String s) throws Exception{

       Future<Channel> channelFuture= pool.acquire().syncUninterruptibly().await();
      Channel channel= channelFuture.get();
      channel.writeAndFlush(Unpooled.copiedBuffer(s+"helo", CharsetUtil.UTF_8));

      pool.release(channel);


    }

    public void initBootStrap() throws InterruptedException {

        EventLoopGroup workerGroup = new NioEventLoopGroup();

            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TimeClientHandler());
                }
            });
            b.remoteAddress(host,port);

            //自定义的channelpoolhandler
            ChannelPoolHandler handler = new ChannelPoolHandler() {
                @Override
                public void channelReleased(Channel ch) throws Exception {

                    System.out.println("释放" + ch);
                }

                @Override
                public void channelAcquired(Channel ch) throws Exception {
                    System.out.println("获取" + ch);

                }

                @Override
                public void channelCreated(Channel ch) throws Exception {
                    System.out.println("创建" + ch);

                }
            };

            pool = new FixedChannelPool(b, handler, 20);

            Channel channel = b.connect(host, port).channel(); // (5)

            channel.writeAndFlush(Unpooled.copiedBuffer("hello", CharsetUtil.UTF_8));

    }






    public static void main(String args[]) throws Exception {
        String host = "localhost";
        int port = 9003;

        SimpleClient simpleClient=new SimpleClient(host,port);
        simpleClient.initBootStrap();

        Executor executor= Executors.newFixedThreadPool(20);
        executor.execute(()->{
            try {
                simpleClient.request("hansongjiang.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


//        executor.execute(()->{
//            try {
//                simpleClient.request("thinker.");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
    }


}

