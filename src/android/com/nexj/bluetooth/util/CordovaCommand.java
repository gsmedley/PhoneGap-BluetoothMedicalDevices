package com.nexj.bluetooth.util;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.PluginResult;

import com.nexj.bluetooth.MedicalDevicePlugin;

public abstract class CordovaCommand extends RunnableCommand
{
    protected final MedicalDevicePlugin m_plugin;
    protected final CallbackContext m_callback;

    public CordovaCommand(MedicalDevicePlugin plugin, CallbackContext callbackContext)
    {
        super();

        m_plugin = plugin;
        m_callback = callbackContext;
    }

    public void run()
    {
        try
        {
            execute();
        }
        catch (Exception e)
        {
            returnResult(ResultFactory.createResult(PluginResult.Status.ERROR, e.getMessage()));

            e.printStackTrace();
        }
    }

    protected void returnResult(final PluginResult result)
    {
        m_plugin.returnResult(result, m_callback);
    }
}
