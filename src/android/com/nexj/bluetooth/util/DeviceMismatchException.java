package com.nexj.bluetooth.util;

import java.io.IOException;

public class DeviceMismatchException extends IOException
{
    private static final long serialVersionUID = 6242133195307027611L;

    public DeviceMismatchException(String message)
    {
        super(message);
    }
}
