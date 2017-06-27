package com.hsj.netty.demo;

import com.hsj.netty.demo.client.request.RpcRequest;
import com.hsj.netty.demo.client.serialize.FastJsonSerialize;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * Created by hanhansongjiang on 17/6/15.
 */
public class SimpleServerHandler  extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        byte[] bytes=new byte[byteBuf.readableBytes()];


        int i=0;
        while(byteBuf.isReadable()){

            bytes[i]=byteBuf.readByte();
            i++;
        }

        FastJsonSerialize fastJsonSerialize=new FastJsonSerialize();

         RpcRequest request= fastJsonSerialize.deSerialize(bytes, RpcRequest.class);

        System.out.println(request);



    }
}
