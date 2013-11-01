package com.nexj.bluetooth.util;

import com.nexj.bluetooth.BluetoothDeviceAdapter;

public class StopCommand extends RunnableCommand
{
    public void execute() throws Exception
    {
        BluetoothDeviceAdapter.stop();
    }
}