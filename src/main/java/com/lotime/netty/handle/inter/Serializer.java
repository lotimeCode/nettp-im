package com.lotime.netty.handle.inter;

import com.lotime.netty.common.SerializerAlgorithm;
import com.lotime.netty.handle.JSONSerializer;

/**
 * @author wangzhimin
 * @version create 2018/9/28 10:09
 */
public interface Serializer {

    byte DEFAULT_SERIALIZER = SerializerAlgorithm.JSON;

    Serializer DEFAULT = new JSONSerializer();

    /**
     * 序列化算法
     */
    byte getSerializerAlgorithm();

    /**
     * java 对象转换成二进制
     */
    byte[] serialize(Object object);

    /**
     * 二进制转换成 java 对象
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
