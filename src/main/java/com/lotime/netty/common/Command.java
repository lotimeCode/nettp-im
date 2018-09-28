package com.lotime.netty.common;

/**
 * @author wangzhimin
 * @version create 2018/9/28 10:03
 */
public interface Command {

    Byte LOGIN_REQ  = 1;

    Byte LOGIN_RES = 2;

    Byte MESS_REQ = 3;

    Byte MESS_RES = 4;
}
