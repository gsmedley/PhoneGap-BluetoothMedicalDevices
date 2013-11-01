package com.nexj.bluetooth.parser.bluglu;

public class MessageTimeout
{
    public static final byte[] HEADER = new byte[]{0x01, 0x06, 0x02, 0x00};
    public static final byte TIMEOUT = 0x00;
}
