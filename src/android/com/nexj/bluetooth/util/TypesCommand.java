package com.nexj.bluetooth.util;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.PluginResult;

import com.nexj.bluetooth.BluetoothDeviceAdapter;
import com.nexj.bluetooth.MedicalDevicePlugin;

public class TypesCommand extends CordovaCommand
{
    public TypesCommand(MedicalDevicePlugin plugin, CallbackContext callBack)
    {
        super(plugin, callBack);
    }

    public void execute()
    {
        returnResult(ResultFactory.createResult(PluginResult.Status.OK, ResultFactory.mapToJSON(BluetoothDeviceAdapter.BRAND_PARSER_TYPES_MAP)));
    }
}
