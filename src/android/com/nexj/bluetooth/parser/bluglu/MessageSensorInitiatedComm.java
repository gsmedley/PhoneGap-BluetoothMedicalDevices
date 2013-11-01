package com.nexj.bluetooth.parser.bluglu;

public class MessageSensorInitiatedComm
{
    public static final byte[] HEADER = new byte[]{0x01, 0x06, 0x02, 0x00};
    public byte m_nPayloadLength;
    public byte[] m_nPayload;

    MessageSensorInitiatedComm(byte length, byte[] payload)
    {
        m_nPayloadLength = length;
        m_nPayload = new byte[length];
        System.arraycopy(payload, 0, m_nPayload, 0, length);
    }
}
