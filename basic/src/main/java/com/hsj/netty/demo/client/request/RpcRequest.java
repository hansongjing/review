package com.hsj.netty.demo.client.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by hanhansongjiang on 17/6/27.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RpcRequest

{
    private String rpcId;

    private String className;

    private String methodName;

    private Object[] args;


    private String version="1.0";


    private Class<?>[] argTypes;

}
