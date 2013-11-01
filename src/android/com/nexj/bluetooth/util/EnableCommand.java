package com.nexj.bluetooth.util;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.PluginResult;

import android.bluetooth.BluetoothAdapter;

import com.nexj.bluetooth.BluetoothDeviceAdapter;
import com.nexj.bluetooth.MedicalDevicePlugin;

public class EnableCommand extends CordovaCommand
{
    public EnableCommand(MedicalDevicePlugin plugin, CallbackContext callBack)
    {
        super(plugin, callBack);
    }

    public void execute() throws Exception
    {
        if (BluetoothDeviceAdapter.isDisabled())
        {
            m_plugin.registerCommandForAction(BluetoothAdapter.ACTION_STATE_CHANGED, new RunnableCommand()
            {
                public void execute()
                {
                    returnResult(ResultFactory.createResult(PluginResult.Status.OK, true));
                }
            }, BluetoothAdapter.STATE_ON);

            BluetoothDeviceAdapter.enable(m_plugin.m_activity);
        }
        else
        {
            returnResult(ResultFactory.createResult(PluginResult.Status.OK, true));
        }
    }
}
