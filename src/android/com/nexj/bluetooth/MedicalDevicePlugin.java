package com.nexj.bluetooth;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.nexj.bluetooth.util.BondedDevicesCommand;
import com.nexj.bluetooth.util.ConnectCommand;
import com.nexj.bluetooth.util.DisableCommand;
import com.nexj.bluetooth.util.DisconnectCommand;
import com.nexj.bluetooth.util.DiscoverableCommand;
import com.nexj.bluetooth.util.EnableCommand;
import com.nexj.bluetooth.util.IsEnabledCommand;
import com.nexj.bluetooth.util.IsSupportedCommand;
import com.nexj.bluetooth.util.ResultFactory;
import com.nexj.bluetooth.util.StopCommand;
import com.nexj.bluetooth.util.TypesCommand;

public class MedicalDevicePlugin extends CordovaPlugin {

	public static final String PLUGIN_NAME = "MedicalDevicePlugin";

    public static final String ACTION_ENABLE = "enable";
    public static final String ACTION_DISABLE = "disable";
    public static final String ACTION_REQUEST_DISCOVERABLE = "discoverable";
    public static final String ACTION_GETBONDEDDEVICES = "getBonded";
    public static final String ACTION_IS_ENABLED = "isEnabled";
    public static final String ACTION_IS_SUPPORTED = "isSupported";
    public static final String ACTION_GET_TYPES = "types";
    public static final String ACTION_CONNECT = "connect";
    public static final String ACTION_DISCONNECT = "disconnect";

    public  Activity m_activity;
    protected  ExecutorService m_threadPool;

    protected  BroadcastReceiver m_broadcastReceiver;
    protected  List<FutureTask<Runnable>> m_interruptableTasks;
    protected  Map<String, Vector<Runnable>> m_actionCallbackMap;
    protected  Map<Runnable, Integer> m_actionStateMap;
    protected  Map<String, BluetoothDeviceAdapter> m_adapterMap;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize( cordova, webView );

        m_activity = cordova.getActivity();
        m_threadPool = cordova.getThreadPool();

        m_interruptableTasks = new Vector<FutureTask<Runnable>>(16);
        m_broadcastReceiver = new BluetoothBroadcastReceiver();
        m_actionCallbackMap = new ConcurrentHashMap<String, Vector<Runnable>>(8);
        m_actionStateMap = new ConcurrentHashMap<Runnable, Integer>(8);
        m_adapterMap = new HashMap<String, BluetoothDeviceAdapter>(BluetoothDeviceAdapter.BRAND_ADAPTER_CLASS_MAP.size());

        Iterator<Entry<String, String>> itr = BluetoothDeviceAdapter.BRAND_ADAPTER_CLASS_MAP.entrySet().iterator();

        while (itr.hasNext())
        {
            try
            {
                Entry<String, String> entry = itr.next();

                m_adapterMap.put(entry.getKey(), (BluetoothDeviceAdapter)Class.forName(entry.getValue()).newInstance());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

        m_activity.registerReceiver(m_broadcastReceiver, intentFilter);
    }

    public void onDestroy()
    {
        m_activity.unregisterReceiver(m_broadcastReceiver);

        Iterator<Entry<String, BluetoothDeviceAdapter>> itr = m_adapterMap.entrySet().iterator();

        while (itr.hasNext())
        {
            BluetoothDeviceAdapter adapter = itr.next().getValue();

            adapter.disconnect(null);
        }

        new StopCommand().run();
    }

    public void registerCommandForAction(String action, Runnable command, int state)
    {
        Vector<Runnable> callbackList = m_actionCallbackMap.get(action);

        if (callbackList == null)
        {
            callbackList = new Vector<Runnable>(8);
        }

        callbackList.add(command);

        m_actionCallbackMap.put(action, callbackList);

        if (state > -1)
        {
            m_actionStateMap.put(command, Integer.valueOf(state));
        }
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
    {
        if (ACTION_CONNECT.equals(action))
        {
            JSONObject params = args.optJSONObject(0);
            String brand = params.optString("brand");

            if (m_adapterMap.containsKey(brand))
            {
                execute(new ConnectCommand(this, m_adapterMap.get(brand), callbackContext, ResultFactory.JSONToMap(params)), true);
            }
            else
            {
                return false;
            }
        }
        else if (ACTION_DISCONNECT.equals(action))
        {
            JSONObject params = args.optJSONObject(0);
            String brand = params.optString("brand");

            if (m_adapterMap.containsKey(brand))
            {
                execute(new DisconnectCommand(this, m_adapterMap.get(brand), callbackContext));
            }
            else
            {
                return false;
            }
        }
        else if (ACTION_ENABLE.equals(action))
        {
            execute(new EnableCommand(this, callbackContext));
        }
        else if (ACTION_DISABLE.equals(action))
        {
            execute(new DisableCommand(this, callbackContext));
        }
        else if (ACTION_REQUEST_DISCOVERABLE.equals(action))
        {
            execute(new DiscoverableCommand(this, callbackContext, args.optInt(0)));
        }
        else if (ACTION_IS_SUPPORTED.equals(action))
        {
            execute(new IsSupportedCommand(this, callbackContext));
        }
        else if (ACTION_GETBONDEDDEVICES.equals(action))
        {
            // TODO: this is a hack
            execute(new BondedDevicesCommand(this, m_adapterMap.get("AND"), callbackContext));
        }
        else if (ACTION_IS_ENABLED.equals(action))
        {
            execute(new IsEnabledCommand(this, callbackContext));
        }
        else if (ACTION_GET_TYPES.equals(action))
        {
            execute(new TypesCommand(this, callbackContext));
        }
        else
        {
            return false;
        }

        return true;
    }

    public void returnResult(final PluginResult result, final CallbackContext callbackContext)
    {
        m_activity.runOnUiThread(new Runnable()
        {
            public void run()
            {
                callbackContext.sendPluginResult(result);
            }
        });
    }

    public void execute(final Runnable command)
    {
        execute(command, false);
    }

    public void execute(final Runnable command, boolean interruptable)
    {
        if (!m_threadPool.isShutdown())
        {
            if (interruptable)
            {
                FutureTask<Runnable> task = new FutureTask<Runnable>(command, null)
                            {
                    protected void done()
                    {
                        m_interruptableTasks.remove(this);
                    }
                            };

                            m_interruptableTasks.add(task);

                            m_threadPool.execute(task);
            }
            else
            {
                m_threadPool.execute(command);
            }
        }
    }

    public void interrupt()
    {
        Iterator<FutureTask<Runnable>> itr = m_interruptableTasks.iterator();

        while (itr.hasNext())
        {
            FutureTask<Runnable> task = itr.next();

            if (!task.isDone())
            {
                task.cancel(true);
            }
        }

        if (!m_threadPool.isShutdown())
        {
            if (m_threadPool instanceof ThreadPoolExecutor)
            {
                ((ThreadPoolExecutor)m_threadPool).purge();
            }
        }

        m_interruptableTasks.clear();
    }

    protected class BluetoothBroadcastReceiver extends BroadcastReceiver
    {
        public void onReceive(Context context, Intent intent)
        {
            String action;
            Vector<Runnable> callbackList;

            if ((action = intent.getAction()) == null
                        || (callbackList = m_actionCallbackMap.get(action)) == null
                        || callbackList.isEmpty())
            {
                return;
            }

            Iterator<Runnable> itr = callbackList.iterator();

            while (itr.hasNext())
            {
                Runnable command = itr.next();
                Integer state;

                if ((state = m_actionStateMap.get(command)) == null)
                {
                    continue;
                }

                if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action))
                {
                    int curr = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, -1);

                    if ((BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE == intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE, -1)
                                || BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE == curr)
                                && state.intValue() == curr)
                    {
                        execute(command);
                    }
                }
                else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action))
                {
                    if (state.intValue() == intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1))
                    {
                        execute(command);
                    }
                }
            }
        }
    }
}
