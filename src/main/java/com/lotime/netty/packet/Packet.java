package com.lotime.netty.packet;

/**
 * @author wangzhimin
 * @version create 2018/9/28 9:59
 */
public abstract class Packet {
    public byte version = 1;

    public abstract Byte getCommand();
}
