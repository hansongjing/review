package com.hsj.netty.demo.client.request;

import lombok.Data;

/**
 * Created by hanhansongjiang on 17/6/27.
 */
@Data
public class RpcResponse {

    private String responseId;

    private String error;

    private  Object res;

    private Class resType;


}
