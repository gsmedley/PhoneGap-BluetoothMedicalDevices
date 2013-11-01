package com.nexj.bluetooth.parser.AND;

/**
 * This defines the BloodPressure object. Like the Weight object it first contains the header which has basic information
 * like when the measurement was taken etc. see ANDHeader.java for more information.
 * Note the _SysDia field really is systolic - diastolic pressure so to get systolic, the value in this field is added
 * to the value in diastolic. Otherwise all fields are relatively self explanatory
 */
public class ANDBloodPressure extends ANDPacket
{
    public String m_sValid; // "80" is valid measurement - 2 Bytes
    public int m_nSysDia; // Systolic - Diastolic (2 Bytes)
    public int m_nDia; // Diastolic - (2 Bytes)
    public int m_nPulse; // Pulse Rate per minute (2 Bytes)
    public int m_nMAP; // Mean Arterial Pressure (2 Bytes)
}
