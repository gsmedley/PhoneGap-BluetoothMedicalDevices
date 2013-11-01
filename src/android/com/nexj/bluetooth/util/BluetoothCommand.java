package com.nexj.bluetooth.util;

import org.apache.cordova.CallbackContext;

import com.nexj.bluetooth.BluetoothDeviceAdapter;
import com.nexj.bluetooth.MedicalDevicePlugin;

public abstract class BluetoothCommand extends CordovaCommand
{
    protected final BluetoothDeviceAdapter m_adapter;

    public BluetoothCommand(MedicalDevicePlugin plugin, BluetoothDeviceAdapter adapter, CallbackContext callbackContext)
    {
        super(plugin, callbackContext);

        m_adapter = adapter;
    }
}
