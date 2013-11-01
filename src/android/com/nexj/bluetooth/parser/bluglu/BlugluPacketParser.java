package com.nexj.bluetooth.parser.bluglu;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.nexj.bluetooth.parser.PacketParser;

public class BlugluPacketParser extends PacketParser<BlugluHeader, BlugluPacket>
{
    protected static final String[] SUPPORTED_DEVICE_TYPES;

    protected int m_nExpectedDevicePacketSize = 0;

    static
    {
        SUPPORTED_DEVICE_TYPES = new String[]{"blublu"};
    }

    public BlugluPacketParser()
    {

        super(BlugluPacketParser.class.getName());
    }

    public static JSONObject dataToJSON(InputStream is, OutputStream os) throws JSONException
    {
        JSONObject payload = new JSONObject();

        // Todo

        return payload;
    }

    protected HashMap<String, Object> parsePacket(BlugluHeader packet) throws JSONException
    {
        // TODO Auto-generated method stub
        return null;
    }

    protected BlugluHeader createPacket(BlugluPacket header, InputStream is) throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    protected BlugluPacket createHeader(InputStream is) throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getSupportedDeviceTypes()
    {
        return SUPPORTED_DEVICE_TYPES;
    }
}
