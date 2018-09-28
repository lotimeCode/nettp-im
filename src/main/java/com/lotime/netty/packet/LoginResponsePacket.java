package com.lotime.netty.packet;

import com.lotime.netty.common.Command;
import lombok.Data;

/**
 * @author wangzhimin
 * @version create 2018/9/28 19:23
 */
@Data
public class LoginResponsePacket extends Packet {

    private boolean success;

    private String reason;


    @Override
    public Byte getCommand() {
        return Command.LOGIN_RES;
    }
}
