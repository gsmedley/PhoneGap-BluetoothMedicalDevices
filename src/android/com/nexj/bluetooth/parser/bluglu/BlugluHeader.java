package com.nexj.bluetooth.parser.bluglu;

public class BlugluHeader
{
    public int m_nPStartOfPacket;// Always 0x01
    public int m_nLengthOfPacket; // (1 byte)
    public int m_nMsgCenterCategory; // Always 0x02
    public int m_nMessageID; // (1 Byte)
}
