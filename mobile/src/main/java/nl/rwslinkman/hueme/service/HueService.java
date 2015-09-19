package nl.rwslinkman.hueme.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.philips.lighting.hue.listener.PHGroupListener;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHNotificationManager;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.exception.PHHueException;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLightState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.rwslinkman.hueme.R;

/**
 * HueService
 * Keeps up with the Hue SDK instance
 * @author Rick Slinkman
 */
public class HueService extends Service implements PHSDKListener
{
    // Debug name
    public static final String TAG = HueService.class.getSimpleName();
    // State constants
    public static final int STATE_IDLE = 0;
    public static final int STATE_SCANNING = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final String DISPLAY_NO_BRIDGE_STATE = "display.no.bridge.state";
    public static final String SCANNING_STARTED = "ap.scanning.started";
    public static final String HUE_AP_FOUND = "hue.ap.found";
    public static final String HUE_AP_REQUIRES_PUSHLINK = "ap.requires.pushlink";
    public static final String HUE_AP_NOTRESPONDING = "hue.ap.notresponding";
    public static final String HUE_HEARTBEAT_UPDATE = "hue.heartbeat.update";
    public static final String HUE_GROUPSTATE_UPDATE = "hue.groupstate.update";
    public static final String BRIDGE_CONNECTED = "hue.bridge.connected";
    public static final String INTENT_EXTRA_ACCESSPOINTS_IP = "hueservice.extra.accesspoints.ip";
    public static final String INTENT_EXTRA_PUSHLINK_IP = "hueservice.extra.pushlink.ip";
    private static final String AP_USERNAME = "hue-and-me-app";
    // Class variables
    private final IBinder mBinder = new LocalBinder();
    private PHHueSDK phHueSDK;
    private HueSharedPreferences prefs;
    private Map<String,PHAccessPoint> mAccessPoints;
    private Set<BroadcastReceiver> mBroadcastReceivers;
    private int currentServiceState;

    @Override
    public void onCreate()
    {
        super.onCreate();

        this.mBroadcastReceivers = new HashSet<>();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        currentServiceState = HueService.STATE_IDLE;

        phHueSDK = PHHueSDK.getInstance();
        phHueSDK.setAppName(getString(R.string.app_name));

        PHNotificationManager phNotificationManager = phHueSDK.getNotificationManager();
        phNotificationManager.registerSDKListener(this);

        prefs = HueSharedPreferences.getInstance(getApplicationContext());
        String lastIpAddress   = prefs.getLastConnectedIPAddress();
        String lastUsername    = prefs.getUsername();

        // Automatically try to connect to the last connected IP Address.  For multiple bridge support a different implementation is required.
        if (lastIpAddress != null && !lastIpAddress.equals(""))
        {
            Log.d(TAG, "Service loading stored AP");
            PHAccessPoint lastAccessPoint = new PHAccessPoint();
            lastAccessPoint.setIpAddress(lastIpAddress);
            lastAccessPoint.setUsername(lastUsername);

            if (!phHueSDK.isAccessPointConnected(lastAccessPoint))
            {
                Log.d(TAG, "Connecting to stored accesspoint");
                phHueSDK.connect(lastAccessPoint);
                this.currentServiceState = STATE_CONNECTING;
            }
            else
            {
                Log.d(TAG, "AP is connected");
            }
        }
        else
        {  // First time use, so perform a bridge search.
            this.startScanning();
        }
        return mBinder;
    }

    public void startScanning()
    {
        PHBridgeSearchManager phSearchManager = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        phSearchManager.search(true, true);

        this.currentServiceState = HueService.STATE_SCANNING;
        Intent intent = new Intent(HueService.SCANNING_STARTED);
        this.sendBroadcast(intent);
    }

    public int getCurrentServiceState()
    {
        return this.currentServiceState;
    }

    public void connectToAccessPoint(String ipAddress)
    {
        this.currentServiceState = HueService.STATE_CONNECTING;
        try
        {
            PHAccessPoint chosenAP = this.mAccessPoints.get(ipAddress);
            this.phHueSDK.connect(chosenAP);
        }
        catch(PHHueException phe)
        {
            // Already connected to AP
            PHBridge bridge = this.phHueSDK.getSelectedBridge();
            this.currentServiceState = HueService.STATE_CONNECTED;
            Log.d(TAG, "Bridge found via exception: " + Boolean.toString(bridge != null));
        }
    }

    public void startPushlink(String ipAddress)
    {
        this.currentServiceState = HueService.STATE_CONNECTING;
        Log.d(TAG, "Start pushlink");

        PHAccessPoint accessPoint = mAccessPoints.get(ipAddress);
        if(accessPoint != null)
        {
            this.phHueSDK.startPushlinkAuthentication(accessPoint);
        }
    }

    @Override
    public void onCacheUpdated(final List<Integer> cacheNotificationsList, PHBridge phBridge)
    {
        Log.d(TAG, "Hue bridge cache updated");
        if(!cacheNotificationsList.isEmpty())
        {
            if (Looper.myLooper() == null)
            {
                Looper.prepare();
            }
            new Handler().post(new Runnable()
            {
                @Override
                public void run()
                {

                    HueService.this.analyzeCacheNotifications(cacheNotificationsList);
                }
            });
        }
    }

    private void analyzeCacheNotifications(List<Integer> cacheNotificationsList)
    {
        for(int notification : cacheNotificationsList)
        {
            if(notification == PHMessageType.LIGHTS_CACHE_UPDATED)
            {
                Log.d(TAG, "LIGHTS ARE UPDATED!");
            }
            else if(notification == PHMessageType.GROUPS_CACHE_UPDATED)
            {
                Log.d(TAG, "GROUPS ARE UPDATED!");
            }
            else
            {
                Log.d(TAG, "Cache notification: " + notification);
            }
        }
    }

    @Override
    public void onBridgeConnected(PHBridge phBridge)
    {
        Log.d(TAG, "Bridge connected");
        this.phHueSDK.setSelectedBridge(phBridge);
        // Handle heartbeat
        this.phHueSDK.enableHeartbeat(phBridge, PHHueSDK.HB_INTERVAL);
        this.phHueSDK.getLastHeartbeat().put(phBridge.getResourceCache().getBridgeConfiguration().getIpAddress(), System.currentTimeMillis());
        // Store connection info
        this.prefs.setLastConnectedIPAddress(phBridge.getResourceCache().getBridgeConfiguration().getIpAddress());
        this.prefs.setUsername(AP_USERNAME);
        // Subscribe to events

        // Inform listeners
        Intent intent = new Intent(HueService.BRIDGE_CONNECTED);
        this.sendBroadcast(intent);
        this.currentServiceState = HueService.STATE_CONNECTED;
    }

    @Override
    public void onAuthenticationRequired(PHAccessPoint phAccessPoint)
    {
        this.currentServiceState = HueService.STATE_IDLE;
        Intent intent = new Intent(HueService.HUE_AP_REQUIRES_PUSHLINK);
        intent.putExtra(HueService.INTENT_EXTRA_PUSHLINK_IP, phAccessPoint.getIpAddress());
        this.sendBroadcast(intent);
    }

    @Override
    public void onAccessPointsFound(List<PHAccessPoint> list)
    {
        mAccessPoints = new HashMap<>();
        for(PHAccessPoint ap : list)
        {
            ap.setUsername(HueService.AP_USERNAME);
            mAccessPoints.put(ap.getIpAddress(), ap);
        }

        ArrayList<String> ipAddresses = new ArrayList<>(mAccessPoints.keySet());

        Intent intent = new Intent(HueService.HUE_AP_FOUND);
        intent.putStringArrayListExtra(INTENT_EXTRA_ACCESSPOINTS_IP, ipAddresses);
        this.sendBroadcast(intent);
        this.currentServiceState = HueService.STATE_IDLE;
    }

    @Override
    public void onError(int errorCode, String errorMessage)
    {
        Intent intent = new Intent();
        switch (errorCode)
        {
            case PHMessageType.BRIDGE_NOT_FOUND:
                // Broadcast to listeners
                intent.setAction(HueService.DISPLAY_NO_BRIDGE_STATE);
                this.sendBroadcast(intent);
                break;
            case PHHueError.BRIDGE_NOT_RESPONDING:
                // Broadcast message
                intent.setAction(HueService.HUE_AP_NOTRESPONDING);
                this.sendBroadcast(intent);
                break;
            default:
                Log.e(TAG, "Hue SDK error: " + errorMessage);
                break;
        }

    }

    @Override
    public void onConnectionResumed(PHBridge phBridge)
    {
        Log.d(TAG, "Connection resumed with Hue bridge (heartbeat)");
        phHueSDK.setSelectedBridge(phBridge);
        String resoucheIPaddress = phBridge.getResourceCache().getBridgeConfiguration().getIpAddress();
        phHueSDK.getLastHeartbeat().put(resoucheIPaddress, System.currentTimeMillis());
        for (int i = 0; i < phHueSDK.getDisconnectedAccessPoint().size(); i++)
        {
            if (phHueSDK.getDisconnectedAccessPoint().get(i).getIpAddress().equals(resoucheIPaddress))
            {
                phHueSDK.getDisconnectedAccessPoint().remove(i);
            }
        }

        Intent intent = new Intent(HueService.HUE_HEARTBEAT_UPDATE);
        this.sendBroadcast(intent);
    }

    @Override
    public void onConnectionLost(PHAccessPoint phAccessPoint) {
        Log.d(TAG, "Connection to Hue access point lost");
        if (!phHueSDK.getDisconnectedAccessPoint().contains(phAccessPoint))
        {
            phHueSDK.getDisconnectedAccessPoint().add(phAccessPoint);
        }
    }

    @Override
    public void onParsingErrors(List<PHHueParsingError> parsingErrorsList)
    {
        Log.e(TAG, "Parsing errors in Philips SDK");
        for (PHHueParsingError parsingError: parsingErrorsList)
        {
            Log.e(TAG, "ParsingError: " + parsingError.getMessage());
        }
    }

    public PHBridge getBridge()
    {
        return this.phHueSDK.getSelectedBridge();
    }

    public class LocalBinder extends Binder
    {
        public HueService getService()
        {
            return HueService.this;
        }
    }

    @Override
    public void onDestroy()
    {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null)
        {
            if (phHueSDK.isHeartbeatEnabled(bridge))
            {
                phHueSDK.disableHeartbeat(bridge);
            }

            phHueSDK.disconnect(bridge);
            super.onDestroy();
        }
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter)
    {
        this.mBroadcastReceivers.add(receiver);
        return super.registerReceiver(receiver, filter);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver)
    {
        if(this.mBroadcastReceivers.contains(receiver))
        {
            this.mBroadcastReceivers.remove(receiver);
            super.unregisterReceiver(receiver);
        }
    }
}