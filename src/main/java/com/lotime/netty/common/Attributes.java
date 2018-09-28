package com.lotime.netty.common;

import io.netty.util.AttributeKey;

/**
 * @author wangzhimin
 * @version create 2018/9/28 20:18
 */
public interface Attributes {
    AttributeKey<Boolean> LOGIN = AttributeKey.newInstance("login");
}
