package com.nexj.bluetooth.util;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.PluginResult;

import com.nexj.bluetooth.BluetoothDeviceAdapter;
import com.nexj.bluetooth.MedicalDevicePlugin;

public class BondedDevicesCommand extends BluetoothCommand
{
    public BondedDevicesCommand(MedicalDevicePlugin plugin, BluetoothDeviceAdapter adapter, CallbackContext callBack)
    {
        super(plugin, adapter, callBack);
    }

    public void execute() throws Exception
    {
        returnResult(ResultFactory.createResult(PluginResult.Status.OK, m_adapter.getBondedDevices()));
    }
}
