package com.hsj.netty.demo.client.serialize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.deserializer.JSONPDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.util.CharsetUtil;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by hanhansongjiang on 17/6/27.
 */
public class FastJsonSerialize  implements serialize{
    @Override
    public byte[] serialize(Object object) {
        System.out.println("FastjsonSerializer Serializer");
        return JSON.toJSONBytes(object, SerializerFeature.SortField);
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> clazz) throws IOException {
        System.out.println("FastjsonSerializer Deserializer");
        return JSON.parseObject(bytes, clazz, Feature.SortFeidFastMatch);
    }
}
