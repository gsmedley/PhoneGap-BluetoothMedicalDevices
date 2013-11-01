package com.nexj.bluetooth.parser.bluglu;

public class MessageSensorConnState
{
    public static final byte[] HEADER = new byte[]{0x01, 0x06, 0x02, 0x00};
    public static final byte STATE_SENSOR_DETACHED = 0x00;
    public static final byte STATE_SENSOR_ATTACHED = 0x01;
}
