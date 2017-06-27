package com.hsj.netty.demo.client.serialize;

import com.hsj.netty.demo.client.request.RpcRequest;

import java.util.Arrays;

/**
 * Created by hanhansongjiang on 17/6/27.
 */
public class Test {

    public static void main(String args[]) throws  Exception{
        FastJsonSerialize fastJsonSerialize=new FastJsonSerialize();

        RpcRequest request=RpcRequest.builder()
                  .rpcId("001")
                   .version("2.0")
                   .args(new Object[]{"helllo",25})
                    .argTypes(new Class[]{String.class,Integer.class})
                    .build();

      byte[]  ans=  fastJsonSerialize.serialize(request);

     RpcRequest request1= fastJsonSerialize.deSerialize(ans,RpcRequest.class);

     System.out.println(request1);

    }
}
