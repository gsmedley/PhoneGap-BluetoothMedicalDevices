package com.nexj.bluetooth.util;

import java.util.HashMap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult.Status;

import android.bluetooth.BluetoothAdapter;

import com.nexj.bluetooth.BluetoothDeviceAdapter;
import com.nexj.bluetooth.MedicalDevicePlugin;

public class DiscoverableCommand extends CordovaCommand
{
    protected int m_nDuration;

    public DiscoverableCommand(MedicalDevicePlugin plugin, CallbackContext callBack, int time)
    {
        super(plugin, callBack);

        m_nDuration = time > 0 && time <= 300 ? time : 120;
    }

    public void execute() throws Exception
    {
        if (BluetoothDeviceAdapter.isDiscoverable())
        {
            BluetoothDeviceAdapter.disableDiscoverable();
        }

        m_plugin.registerCommandForAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED, new RunnableCommand()
        {
            public void execute() throws Exception
            {
                HashMap<String, Object> payload = new HashMap<String, Object>();
                payload.put("time", m_nDuration);
                payload.put("status", true);

                returnResult(ResultFactory.createResult(Status.OK, payload));
            }
        }, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);

        m_plugin.registerCommandForAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED, new RunnableCommand()
        {
            public void execute() throws Exception
            {
                HashMap<String, Object> payload = new HashMap<String, Object>();
                payload.put("status", false);

                returnResult(ResultFactory.createResult(Status.OK, payload));
            }
        }, BluetoothAdapter.SCAN_MODE_CONNECTABLE);

        m_plugin.registerCommandForAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED, new RunnableCommand()
        {
            public void execute() throws Exception
            {
                HashMap<String, Object> payload = new HashMap<String, Object>();
                payload.put("status", false);

                returnResult(ResultFactory.createResult(Status.OK, payload));
            }
        }, BluetoothAdapter.SCAN_MODE_NONE);

        BluetoothDeviceAdapter.enableDiscovery(m_plugin.m_activity, m_nDuration);
    }
}