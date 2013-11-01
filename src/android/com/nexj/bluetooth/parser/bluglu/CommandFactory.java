package com.nexj.bluetooth.parser.bluglu;

public class CommandFactory
{
    public static byte[] cmdAcknowledgement(int AckForCommandID, int status, int AckDataLength, byte[] AckDataPayload)
    {
        int length = 7 + AckDataLength;
        byte[] Acknowledgement = new byte[length];

        Acknowledgement[0] = 0x01;
        Acknowledgement[1] = 0x08;
        Acknowledgement[2] = 0x00;
        Acknowledgement[3] = 0x00;
        Acknowledgement[4] = (byte)AckForCommandID;
        Acknowledgement[5] = (byte)status;
        Acknowledgement[6] = (byte)AckDataLength;
        System.arraycopy(AckDataPayload, 0, Acknowledgement, 7, AckDataLength);

        return Acknowledgement;
    }

    public static byte[] cmdEcho(byte[] payload)
    {
        int length = 4 + payload.length;
        byte[] echo = new byte[length];

        echo[0] = 0x01;
        echo[1] = (byte)(length);
        echo[2] = 0x00;
        echo[3] = 0x01;
        System.arraycopy(payload, 0, echo, 4, payload.length);

        return echo;
    }

    public static byte[] cmdDisconnect()
    {
        return new byte[]{0x01, 0x04, 0x00, 0x03};
    }

    public static byte[] cmdBatteryStatus()
    {
        return new byte[]{0x01, 0x04, 0x00, 0x04};
    }

    public static byte[] cmdHardwareVersionInfo()
    {
        return new byte[]{0x01, 0x05, 0x00, 0x05, 0x00};
    }

    public static byte[] cmdFirmwareVersionInfo()
    {
        return new byte[]{0x01, 0x05, 0x00, 0x05, 0x01};
    }

    public static byte[] cmdRawData(int sensorId, int responseType, int responseArg, byte[] payload)
    {
        int length = 6 + payload.length;
        byte[] rawData = new byte[length];

        rawData[0] = 0x01;
        rawData[1] = (byte)(length);
        rawData[2] = 0x00;
        rawData[3] = (byte)(sensorId);
        rawData[2] = (byte)(responseType);
        rawData[3] = (byte)(responseArg);
        System.arraycopy(payload, 0, rawData, 6, payload.length);

        return rawData;
    }
}
