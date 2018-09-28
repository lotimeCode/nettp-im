package com.lotime.netty.packet;

import com.lotime.netty.common.Command;
import lombok.Data;

/**
 * @author wangzhimin
 * @version create 2018/9/28 10:03
 */
@Data
public class LoginRequestPacket extends Packet {
    private String userId;

    private String userName;

    private String password;

    @Override
    public Byte getCommand() {
        return Command.LOGIN_REQ;
    }
}
