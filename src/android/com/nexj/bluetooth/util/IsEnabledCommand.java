package com.nexj.bluetooth.util;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.PluginResult;

import com.nexj.bluetooth.BluetoothDeviceAdapter;
import com.nexj.bluetooth.MedicalDevicePlugin;

public class IsEnabledCommand extends CordovaCommand
{
    public IsEnabledCommand(MedicalDevicePlugin plugin, CallbackContext callBack)
    {
        super(plugin, callBack);
    }

    public void execute() throws Exception
    {
        returnResult(ResultFactory.createResult(PluginResult.Status.OK, BluetoothDeviceAdapter.isEnabled()));
    }
}