package com.nexj.bluetooth.util;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.PluginResult;

import com.nexj.bluetooth.BluetoothDeviceAdapter;
import com.nexj.bluetooth.MedicalDevicePlugin;

public class DisconnectCommand extends BluetoothCommand
{
    public DisconnectCommand(MedicalDevicePlugin plugin, BluetoothDeviceAdapter adapter, CallbackContext callbackContext)
    {
        super(plugin, adapter, callbackContext);
    }

    public void execute() throws Exception
    {
        m_plugin.interrupt();

        m_adapter.disconnect(null);

        // Listening ended event
        returnResult(ResultFactory.createResult(PluginResult.Status.OK, false));
    }
}
