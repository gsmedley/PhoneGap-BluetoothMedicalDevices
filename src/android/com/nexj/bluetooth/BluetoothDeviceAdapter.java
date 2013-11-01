package com.nexj.bluetooth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import org.json.JSONObject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import com.nexj.bluetooth.parser.PacketParser;

public abstract class BluetoothDeviceAdapter
{
    protected static BluetoothAdapter BLUETOOTH_ADAPTER;
    protected static List<BluetoothSocket> BLUETOOTH_SOCKET_LIST;

    public static final Map<String, String> BRAND_ADAPTER_CLASS_MAP;
    public static final Map<String, Object> BRAND_PARSER_TYPES_MAP;

    // Service ID required for Bluetooth Serial Port Profile connections
    // Documentation: https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
    protected static final UUID SERVICE_ID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    protected final String m_sLogTag;
    protected final PacketParser<?, ?> m_packetParser;

    static
    {
        // Android 2.3 device did not allow lazy initialization of this value
        BLUETOOTH_ADAPTER = BluetoothAdapter.getDefaultAdapter();

        BRAND_ADAPTER_CLASS_MAP = new HashMap<String, String>(4);
        BRAND_PARSER_TYPES_MAP = new HashMap<String, Object>(4);
        BLUETOOTH_SOCKET_LIST = new Vector<BluetoothSocket>(16);

        // TODO: This is not a scalable option to manage supported devices
        BRAND_ADAPTER_CLASS_MAP.put("AND", ANDBluetoothServer.class.getName());
        BRAND_ADAPTER_CLASS_MAP.put("bluglu", BlugluBluetoothClient.class.getName());
    }

    public BluetoothDeviceAdapter(PacketParser<?, ?> parser, String sLogTag)
    {
        m_packetParser = parser;

        BRAND_PARSER_TYPES_MAP.put(getDeviceManufacturer(), Arrays.asList(parser.getSupportedDeviceTypes()));

        m_sLogTag = sLogTag;
    }

    protected static BluetoothAdapter getAdapter()
    {
        return BLUETOOTH_ADAPTER;
    }

    public static void stop()
    {
        if (isSupported())
        {
            getAdapter().cancelDiscovery();
        }

        Iterator<BluetoothSocket> clientLoop = BLUETOOTH_SOCKET_LIST.iterator();

        while (clientLoop.hasNext())
        {
            BluetoothSocket client = clientLoop.next();

            BLUETOOTH_SOCKET_LIST.remove(client);

            try
            {
                client.close();
            }
            catch (Exception ignored)
            {
                Log.e(BluetoothDeviceAdapter.class.getName(), "Error closing resource: ClientSocket");
            }
        }
    }

    public static boolean isSupported()
    {
        return getAdapter() == null ? false : true;
    }

    public static boolean isEnabled()
    {
        if (isSupported())
        {
            return getAdapter().getState() == BluetoothAdapter.STATE_ON;
        }

        return false;
    }

    public static boolean isDisabled()
    {
        if (isSupported())
        {
            return getAdapter().getState() == BluetoothAdapter.STATE_TURNING_OFF || getAdapter().getState() == BluetoothAdapter.STATE_OFF;
        }

        return false;
    }

    public static void enable(Activity activity)
    {
        if (isSupported())
        {
            activity.startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));

            // This circumvents user interaction to enable Bluetooth
            // getAdapter().enable();
        }
    }

    public static void disable()
    {
        if (isSupported())
        {
            stop();

            getAdapter().disable();
        }
    }

    public static void enableDiscovery(Activity activity, int duration)
    {
        if (isSupported())
        {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
            activity.startActivity(intent);
        }
    }

    public static void disableDiscoverable()
    {
        if (isSupported())
        {
            getAdapter().cancelDiscovery();
        }
    }

    public static boolean isDiscoverable()
    {
        if (isSupported())
        {
            return getAdapter().getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
        }

        return false;
    }

    public List<Object> getBondedDevices() throws Exception
    {
        ArrayList<Object> bondedDevices = new ArrayList<Object>(8);

        if (isSupported())
        {
            Iterator<BluetoothDevice> itr = getAdapter().getBondedDevices().iterator();

            while (itr.hasNext())
            {
                BluetoothDevice bluetoothDevice = itr.next();
                JSONObject deviceInfo = new JSONObject();

                deviceInfo.put("name", bluetoothDevice.getName());
                deviceInfo.put("address", bluetoothDevice.getAddress());
                deviceInfo.put("manufacturer", getDeviceManufacturer());
                deviceInfo.put("protocol", getDeviceProtocol());
                deviceInfo.put("serial", getDeviceSerial(bluetoothDevice));
                deviceInfo.put("type", getDeviceType(bluetoothDevice));

                bondedDevices.add(deviceInfo);
            }
        }

        return bondedDevices;
    }

    protected abstract String getDeviceManufacturer();

    protected abstract String getDeviceProtocol();

    protected abstract String getDeviceSerial(BluetoothDevice device);

    protected abstract String getDeviceType(BluetoothDevice device);

    public abstract boolean isConnected();

    public abstract Map<String, Object> connect(Map<String, Object> params) throws Exception;

    public abstract void disconnect(Map<String, Object> params);
}