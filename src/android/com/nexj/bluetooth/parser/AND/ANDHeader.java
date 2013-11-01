package com.nexj.bluetooth.parser.AND;

/**
 * This defines the Header object which is part of the BloodPressure object and the Weight object.
 * All fields are relatively self explanatory. The date of transmission is used to check against the devices current
 * date to determine if it needs to be reset.
 */
public class ANDHeader
{
    public int m_nPacketType; // Always 2 (2 Bytes)
    public int m_nPacketLength; // exclusive of header (14 - Weight, 10 Blood Pressure) (4 Bytes)
    public int m_nDeviceType; // 767 (x02FF) UA-767PBT, 321 (x0141) UC-321PBT (2 Bytes)
    public int m_nFlag; // (1 Byte)
    public int m_nYearOfMeas; // Year of Measurement (2 Bytes)
    public int m_nMonthOfMeas; // Month of Measurement (1 Byte)
    public int m_nDayOfMeas; // Day of Measurement (1 Byte)
    public int m_nHourOfMeas; // Hour of Measurement (1 Byte)
    public int m_nMinOfMeas; // Minute of Measurement (1 Byte)
    public int m_nSecOfMeas; // Second of Measurement (1 Byte)
    public int m_nYearOfTrans; // Year of Transmission (2 Bytes)
    public int m_nMonthOfTrans; // Month of Transmission (1 Byte)
    public int m_nDayOfTrans; // Day of Transmission (1 Byte)
    public int m_nHourOfTrans; // Hour of Transmission (1 Byte)
    public int m_nMinOfTrans; // Minute of Transmission (1 Byte)
    public int m_nSecOfTrans; // Second of Transmission (1 Byte)
    public String m_sBluetoothId; // Id of remote unit (6 Bytes)
    public String m_sAccessPointId; // Id of Blackberry (6 Bytes always null)
    public String m_sSerialNmber; // Serial number of A&D PBT Series (6 Bytes)
    public byte m_bReserved1; // ?? (1 Bytes)
    public byte m_bReserved2; // ?? (1 Bytes)
    public byte m_bReserved3; // ?? (1 Bytes)
    public byte m_bReserved4; // ?? (1 Bytes)
    public byte m_bReserved5; // ?? (1 Bytes)
    public byte m_bReserved6; // ?? (1 Bytes)
    public byte m_bReserved7; // ?? (1 Bytes)
    public byte m_bReserved8; // ?? (1 Bytes)
    public byte m_bReserved9; // ?? (1 Bytes)
    public byte m_bReserved10; // ?? (1 Bytes)
    public int m_nBatteryStatus; // Battery status - (1 Byte) For BP - value * 0.03 + 2.3
                                 // For Scale - value * 0.02 + 1.9
                                 // inform when below 4.6
    public byte m_bReserved11; // ?? (1 Byte)
    public byte m_bFirmware; // Device Firmware (1 Byte) first 5 bits are firmware, last 3 hardware
                             // i.e. x11 is version 2 of firmware and version 1 of hardware
}
