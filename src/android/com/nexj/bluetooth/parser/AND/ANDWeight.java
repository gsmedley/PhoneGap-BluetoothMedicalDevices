package com.nexj.bluetooth.parser.AND;

/**
 * This defines the Weight object. Like the BloodPressure object it first contains the header which has basic information
 * like when the measurement was taken etc. see ANDHeader.java for more information.
 * All fields are relatively self explanatory.
 */
public class ANDWeight extends ANDPacket
{
    public String m_sLeadBytes; // "ST." - 3 bytes
    public float m_nWeight; // weight in either lbs or kgs depending on next field (7 bytes)
    public String m_sLbsKgs; // indicator of weight value
    public byte m_bCarriageReturn; // contains just a carriage return
    public byte m_bLineFeed; // contains just a line feed
}
