package com.nexj.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.nexj.bluetooth.parser.AND.ANDPacketParser;

public class ANDBluetoothServer extends BluetoothServer
{
    protected final String m_sBrand = "AND";

    public ANDBluetoothServer()
    {
        // Name of the broadcasted service from A&D medical devices
        super(new ANDPacketParser(), ANDBluetoothServer.class.getName(), "PWAccessP");

        Log.i(m_sLogTag, m_sLogTag + " Initialized");
    }

    protected String getDeviceSerial(BluetoothDevice device)
    {
        String name = device.getName();

        if (m_sBrand.equals(name.substring(0, 3))) // Support only AND device serials from bluetooth device names
        {
            return name.substring(6, 16);
        }

        return null;
    }

    protected String getDeviceType(BluetoothDevice device)
    {
        String type = device.getName().substring(4, 6);

        if ("BP".equals(type))
        {
            return "blood pressure";
        }
        else if ("SC".equals(type))
        {
            return "weight";
        }

        return null;
    }

    protected String getDeviceManufacturer()
    {
        return "AND";
    }
}
