package com.nexj.bluetooth.util;

public abstract class RunnableCommand implements Runnable, Command
{
    public void run()
    {
        try
        {
            execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
