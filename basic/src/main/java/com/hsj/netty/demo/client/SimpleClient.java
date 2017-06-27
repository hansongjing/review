package com.hsj.netty.demo.client;

import com.hsj.netty.demo.client.request.RpcRequest;
import com.hsj.netty.demo.client.serialize.FastJsonSerialize;
import com.hsj.netty.demo.client.serialize.serialize;
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

    private serialize  serialize;


    protected GenericObjectPool.Config poolConfig;
    protected PoolableObjectFactory factory;

    public SimpleClient(String host, int port) {
        this.host = host;
        this.port = port;
        serialize=new FastJsonSerialize();
    }


    public void request(RpcRequest rpcRequest) throws Exception{

       Future<Channel> channelFuture= pool.acquire().syncUninterruptibly().await();
      Channel channel= channelFuture.get();
      channel.writeAndFlush(Unpooled.wrappedBuffer(serialize.serialize(rpcRequest)));
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

//            Channel channel = b.connect(host, port).channel(); // (5)
//
//            channel.writeAndFlush(Unpooled.copiedBuffer("hello", CharsetUtil.UTF_8));

    }








    public static void main(String args[]) throws Exception {
        FastJsonSerialize fastJsonSerialize=new FastJsonSerialize();
        String host = "localhost";
        int port = 9003;

        SimpleClient simpleClient=new SimpleClient(host,port);
        simpleClient.initBootStrap();

        Executor executor= Executors.newFixedThreadPool(20);
        executor.execute(()->{
            try {
                RpcRequest request=RpcRequest.builder()
                        .rpcId("001")
                        .version("2.0")
                        .args(new Object[]{"helllo",25})
                        .argTypes(new Class[]{String.class,Integer.class})
                        .build();




                System.out.println(request);
                simpleClient.request(request);
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

