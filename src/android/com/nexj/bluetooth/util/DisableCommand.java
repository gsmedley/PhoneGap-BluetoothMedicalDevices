package com.nexj.bluetooth.util;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import android.bluetooth.BluetoothAdapter;

import com.nexj.bluetooth.BluetoothDeviceAdapter;
import com.nexj.bluetooth.MedicalDevicePlugin;

public class DisableCommand extends CordovaCommand
{
    public DisableCommand(MedicalDevicePlugin plugin, CallbackContext callBack)
    {
        super(plugin, callBack);
    }

    public void execute() throws Exception
    {
        if (BluetoothDeviceAdapter.isEnabled())
        {
            m_plugin.registerCommandForAction(BluetoothAdapter.ACTION_STATE_CHANGED, new CordovaCommand(m_plugin, m_callback)
            {
                public void execute()
                {
                    returnResult(ResultFactory.createResult(PluginResult.Status.OK, true));
                }
            }, BluetoothAdapter.STATE_OFF);

            BluetoothDeviceAdapter.disable();
        }
        else
        {
            returnResult(ResultFactory.createResult(PluginResult.Status.OK, true));
        }
    }
}