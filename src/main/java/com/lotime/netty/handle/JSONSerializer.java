package com.lotime.netty.handle;

import com.alibaba.fastjson.JSON;
import com.lotime.netty.common.SerializerAlgorithm;
import com.lotime.netty.handle.inter.Serializer;

/**
 * @author wangzhimin
 * @version create 2018/9/28 10:13
 */
public class JSONSerializer implements Serializer {
    @Override
    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.JSON;
    }

    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes,clazz);
    }
}
