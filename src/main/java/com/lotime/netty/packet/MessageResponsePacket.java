package com.lotime.netty.packet;

import com.lotime.netty.common.Command;
import lombok.Data;

import java.util.Date;

/**
 * @author wangzhimin
 * @version create 2018/9/28 20:08
 */
@Data
public class MessageResponsePacket extends Packet {
    private Date timestamp;

    private String message;

    @Override
    public Byte getCommand() {
        return Command.MESS_RES;
    }
}
