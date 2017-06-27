package com.hsj.netty.demo.client.serialize;

import java.io.IOException;

/**
 * Created by hanhansongjiang on 17/6/27.
 */
public interface serialize {

    public byte[] serialize(Object object);

    public <T>  T   deSerialize(byte[] bytes,Class<T> tClass) throws IOException;
}
