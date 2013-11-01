package com.nexj.bluetooth.util;

import java.io.IOException;
import java.util.HashMap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.nexj.bluetooth.BluetoothDeviceAdapter;
import com.nexj.bluetooth.MedicalDevicePlugin;

public class ConnectCommand extends BluetoothCommand
{
    protected HashMap<String, Object> m_parameters;

    protected final static Handler MESSAGE_HANDLER;

    static
    {
        MESSAGE_HANDLER = new Handler(Looper.getMainLooper());
    }

    public ConnectCommand(MedicalDevicePlugin plugin, BluetoothDeviceAdapter adapter, CallbackContext callBack, final HashMap<String, Object> params)
    {
        super(plugin, adapter, callBack);

        m_parameters = params;
        m_parameters.put("received", 0);

        int nTimeout = m_parameters.get("time") != null ? (Integer)m_parameters.get("time") : 0;

        if (nTimeout > 0)
        {
            // FIXME: temporarily removed property so copy constructor does not execute this multiple times
            // FIXME: StopListen should not stop all listen commands only this one
            m_parameters.remove("time");

            MESSAGE_HANDLER.postDelayed(new Runnable() // Initiate a stopListenCommand after m_timeout seconds have passed
            {
                public void run()
                {
                    m_plugin.execute(new DisconnectCommand(m_plugin, m_adapter, m_callback));
                }
            }, nTimeout * 1000);
        }

        // Listening started event
        returnResult(ResultFactory.createResult(PluginResult.Status.OK, true));
    }

    protected ConnectCommand(ConnectCommand command)
    {
        super(command.m_plugin, command.m_adapter, command.m_callback);

        m_parameters = command.m_parameters;
    }

    public void execute() throws Exception
    {
        try
        {
            returnResult(ResultFactory.createResult(PluginResult.Status.OK, m_adapter.connect(m_parameters)));

            int nReceivedMeasurements = (Integer)m_parameters.get("received") + 1;
            int nMeasurements = (Integer)m_parameters.get("num");

            if (nMeasurements == 0 || nMeasurements > nReceivedMeasurements) // Stop recurring the listen command is m_measurements have been received
            {
                m_parameters.put("received", nReceivedMeasurements);

                m_plugin.execute(new ConnectCommand(this), true);
            }
        }
        catch (DeviceMismatchException e)
        {
            Log.w(ConnectCommand.class.getName(), e.getMessage());

            m_plugin.execute(new ConnectCommand(this), true); // Repeat execute process when connected device details do not match listen parameters
        }
        catch (IOException e)
        {
            if ("Operation Canceled".equals(e.getMessage()))
            {
                // Surpressed: part of normal operation of stop listening
            }
            else
            {
                throw e;
            }
        }
    }
}