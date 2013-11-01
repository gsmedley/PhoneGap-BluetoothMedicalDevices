package com.nexj.bluetooth.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.json.JSONException;

import android.util.Log;

public abstract class PacketParser<P, H>
{
    protected final String m_sTag;

    public PacketParser(String sTag)
    {
        m_sTag = sTag;
    }

    public Map<String, Object> dataToPayload(InputStream is, OutputStream os, Map<String, Object> params) throws IOException
    {
        preHeader(is, os, (String)params.get("type"));

        H header = null;

        try
        {
            header = createHeader(is);
        }
        catch (IOException e)
        {
            Log.e(m_sTag, "Error occured during header creation", e);

            throw e;
        }

        postHeader(is, os, header);

        preBody(is, os, header);

        P packet = null;

        try
        {
            packet = createPacket(header, is);
        }
        catch (IOException e)
        {
            Log.e(m_sTag, "Error occured during packet creation", e);

            throw e;
        }

        postBody(is, os, packet);

        preParsing(is, os, packet);

        Map<String, Object> payload;

        try
        {
            payload = parsePacket(packet);
        }
        catch (JSONException e)
        {
            Log.e(m_sTag, "Error occured during JSON object creation", e);

            throw new IOException(e.getMessage());
        }

        postParsing(is, os, packet);

        return payload;
    }

    protected void preHeader(InputStream is, OutputStream os, String sDeviceType) throws IOException
    {
    }

    protected void postHeader(InputStream is, OutputStream os, H header) throws IOException
    {
    }

    protected void preBody(InputStream is, OutputStream os, H header) throws IOException
    {
    }

    protected void postBody(InputStream is, OutputStream os, P packet) throws IOException
    {
    }

    protected void preParsing(InputStream is, OutputStream os, P packet) throws IOException
    {
    }

    protected void postParsing(InputStream is, OutputStream os, P packet) throws IOException
    {
    }

    protected abstract Map<String, Object> parsePacket(P packet) throws JSONException;

    protected abstract P createPacket(H header, InputStream is) throws IOException;

    protected abstract H createHeader(InputStream is) throws IOException;

    public abstract String[] getSupportedDeviceTypes();

    // Copies the content of the inputstream into the outputstream
    protected void read(OutputStream os, InputStream is, int nLength) throws IOException
    {
        byte[] bData = new byte[1];

        for (int nCount = 0, nBytes = 0; nCount < nLength; nCount++)
        {
            nBytes = is.read(bData, 0, 1);

            if (nBytes == -1)
            {
                break;
            }

            os.write(bData, 0, 1);
        }
    }
}
