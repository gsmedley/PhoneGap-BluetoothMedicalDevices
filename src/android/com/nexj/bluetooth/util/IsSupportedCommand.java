package com.nexj.bluetooth.util;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import com.nexj.bluetooth.BluetoothDeviceAdapter;
import com.nexj.bluetooth.MedicalDevicePlugin;

public class IsSupportedCommand extends CordovaCommand
{
    public IsSupportedCommand(MedicalDevicePlugin plugin, CallbackContext callBack)
    {
        super(plugin, callBack);
    }

    public void execute() throws Exception
    {
        returnResult(ResultFactory.createResult(PluginResult.Status.OK, BluetoothDeviceAdapter.isSupported()));
    }
}
