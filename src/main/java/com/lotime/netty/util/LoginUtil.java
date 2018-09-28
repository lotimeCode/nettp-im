package com.lotime.netty.util;

import com.lotime.netty.common.Attributes;
import io.netty.channel.Channel;
import io.netty.util.Attribute;

/**
 * @author wangzhimin
 * @version create 2018/9/28 20:14
 */
public class LoginUtil {
    public static void markAsLogin(Channel channel) {
        channel.attr(Attributes.LOGIN).set(true);
    }

    public static boolean hasLogin(Channel channel) {
        Attribute<Boolean> loginAttr = channel.attr(Attributes.LOGIN);

        return loginAttr.get() != null;
    }
}
