package com.nexj.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Map;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.nexj.bluetooth.parser.PacketParser;
import com.nexj.bluetooth.util.DeviceMismatchException;

public abstract class BluetoothServer extends BluetoothDeviceAdapter
{
    // Service name of the server used for Bluetooth discovery process
    protected final String m_sServiceName;

    protected BluetoothServerSocket m_serverSocket;

    public BluetoothServer(PacketParser<?, ?> parser, String sLogTag, String service)
    {
        super(parser, sLogTag);

        m_serverSocket = null;
        m_sServiceName = service;
    }

    public boolean isConnected()
    {
        return m_serverSocket != null;
    }

    public Map<String, Object> connect(Map<String, Object> params) throws Exception
    {
        BluetoothSocket clientSocket = null;
        DataInputStream is = null;
        DataOutputStream os = null;

        try
        {
            if (!isConnected())
            {
                m_serverSocket = getAdapter().listenUsingRfcommWithServiceRecord(m_sServiceName, SERVICE_ID);
            }

            Log.i(m_sLogTag, "Accepting client socket");
            clientSocket = m_serverSocket.accept();

            String sDeviceSerial = (String)params.get("serial");

            if (sDeviceSerial != null && !sDeviceSerial.equals(getDeviceSerial(clientSocket.getRemoteDevice())))
            {
                throw new DeviceMismatchException("Device serial did not match expected");
            }

            BLUETOOTH_SOCKET_LIST.add(clientSocket);

            Log.i(m_sLogTag, "Obtaining client data");
            is = new DataInputStream(clientSocket.getInputStream());
            os = new DataOutputStream(clientSocket.getOutputStream());

            Log.i(m_sLogTag, "Parsing client data");
            return m_packetParser.dataToPayload(is, os, params);
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
            if (clientSocket != null)
            {
                BLUETOOTH_SOCKET_LIST.remove(clientSocket);

                try
                {
                    clientSocket.close();
                }
                catch (Exception ignored)
                {
                    Log.e(m_sLogTag, "Error closing resource: ClientSocket");
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
                m_serverSocket.close();
            }
            catch (Exception ignored)
            {
                Log.e(m_sLogTag, "Error closing resource: BlueToothServerSocket");
            }

            m_serverSocket = null;
        }
    }

    protected String getDeviceProtocol()
    {
        return "server";
    }
}