package com.lotime.netty.packet;

import lombok.Data;

/**
 * @author wangzhimin
 * @version create 2018/9/28 9:59
 */
@Data
public abstract class Packet {
    private byte version = 1;

    public abstract Byte getCommand();
}
