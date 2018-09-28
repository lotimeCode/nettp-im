package com.lotime.netty.packet;

import com.lotime.netty.common.Command;
import lombok.Data;

import java.util.Date;

/**
 * @author wangzhimin
 * @version create 2018/9/28 20:07
 */
@Data
public class MessageRequestPacket extends Packet {

    private Date timestamp;

    private String message;

    @Override
    public Byte getCommand() {
        return Command.MESS_REQ;
    }
}
