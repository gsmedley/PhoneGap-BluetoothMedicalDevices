package com.nexj.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.nexj.bluetooth.parser.bluglu.BlugluPacketParser;

public class BlugluBluetoothClient extends BluetoothClient
{
    protected final String m_sBrand = "bluglu";

    public BlugluBluetoothClient()
    {
        super(new BlugluPacketParser(), BlugluBluetoothClient.class.getName()); ///

        Log.i(m_sLogTag, m_sLogTag + " Initialized");
    }

    protected String getDeviceSerial(BluetoothDevice device)
    {
        return null;
    }

    protected String getDeviceType(BluetoothDevice device)
    {
        String name = device.getName();

        if (m_sBrand.equals(name))
        {
            return name;
        }

        return null;
    }

    protected String getDeviceManufacturer()
    {
        return m_sBrand;
    }

}
