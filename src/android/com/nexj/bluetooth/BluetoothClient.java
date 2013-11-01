package com.nexj.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.nexj.bluetooth.parser.PacketParser;

public abstract class BluetoothClient extends BluetoothDeviceAdapter
{
    protected BluetoothSocket m_clientSocket;

    public BluetoothClient(PacketParser<?, ?> parser, String sLogTag)
    {
        super(parser, sLogTag);

        m_clientSocket = null;
    }

    public boolean isConnected()
    {
        return m_clientSocket != null;
    }

    public Map<String, Object> connect(Map<String, Object> params) throws Exception
    {
        String sDeviceAddress = (String)params.get("address");

        if (sDeviceAddress == null)
        {
            throw new IOException("null MAC adress of medical device");
        }

        if (!isConnected())
        {
            BluetoothDevice mecicalDevice = getAdapter().getRemoteDevice(sDeviceAddress);
            m_clientSocket = mecicalDevice.createRfcommSocketToServiceRecord(SERVICE_ID);

            // Cancel discovery because it will slow down the connection
            getAdapter().cancelDiscovery();

            m_clientSocket.connect();

            BLUETOOTH_SOCKET_LIST.add(m_clientSocket);
        }

        DataInputStream is = null;
        DataOutputStream os = null;

        try
        {
            Log.i(m_sLogTag, "Obtaining client data");
            is = new DataInputStream(m_clientSocket.getInputStream());
            os = new DataOutputStream(m_clientSocket.getOutputStream());

            Log.i(m_sLogTag, "Parsing client data");
            return null; /* m_packetParser.dataToPayload(is, os, params); */
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (Exception ignored)
                {
                    Log.e(m_sLogTag, "Error closing resource: Inputstream");
                }
            }
            if (os != null)
            {
                try
                {
                    os.close();
                }
                catch (Exception ignored)
                {
                    Log.e(m_sLogTag, "Error closing resource: Outputstream");
                }
            }
        }
    }

    public void disconnect(Map<String, Object> params)
    {
        stop();

        if (isConnected())
        {
            try
            {
                m_clientSocket.close();
            }
            catch (Exception ignored)
            {
                Log.e(m_sLogTag, "Error closing resource: BluetoothSocket");
            }

            m_clientSocket = null;
        }
    }

    protected String getDeviceProtocol()
    {
        return "client";
    }
}