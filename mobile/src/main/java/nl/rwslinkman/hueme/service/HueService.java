package nl.rwslinkman.hueme.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHNotificationManager;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.exception.PHHueException;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final String BRIDGE_CONNECTED = "hue.bridge.connected";
    public static final String INTENT_EXTRA_ACCESSPOINTS_IP = "hueservice.extra.accesspoints.ip";
    public static final String INTENT_EXTRA_PUSHLINK_IP = "hueservice.extra.pushlink.ip";
    private static final String AP_USERNAME = "hue-and-me-app";
    // Class variables
    private final IBinder mBinder = new LocalBinder();
    private PHHueSDK phHueSDK;
    private Map<String,PHAccessPoint> mAccessPoints;
    private int currentServiceState;

    @Override
    public IBinder onBind(Intent intent)
    {
        currentServiceState = HueService.STATE_IDLE;

        phHueSDK = PHHueSDK.getInstance();
        phHueSDK.setAppName(getString(R.string.app_name));

        PHNotificationManager phNotificationManager = phHueSDK.getNotificationManager();
        phNotificationManager.registerSDKListener(this);

        PHBridge phHueBridge = phHueSDK.getSelectedBridge();
        if(phHueBridge == null)
        {
            // No bridges found
            this.startScanning();
        }
        else
        {
            phHueSDK.enableHeartbeat(phHueBridge, PHHueSDK.HB_INTERVAL);
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
            Log.d(TAG, "Bridge found via exception: " + Boolean.toString(bridge != null));
        }
    }

    public void startPushlink(String ipAddress)
    {
        this.currentServiceState = HueService.STATE_CONNECTING;

        PHAccessPoint accessPoint = mAccessPoints.get(ipAddress);
        if(accessPoint != null)
        {
            this.phHueSDK.startPushlinkAuthentication(accessPoint);
        }
    }

    @Override
    public void onCacheUpdated(List<Integer> list, PHBridge phBridge)
    {
        Log.d(TAG, "Hue bridge cache updated");
    }

    @Override
    public void onBridgeConnected(PHBridge phBridge)
    {
        this.phHueSDK.setSelectedBridge(phBridge);
        this.phHueSDK.enableHeartbeat(phBridge, PHHueSDK.HB_INTERVAL);

        // TODO: Store bridge for future reference

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
    }

    @Override
    public void onConnectionLost(PHAccessPoint phAccessPoint)
    {
        Log.d(TAG, "Connection to Hue access point lost");
    }

    @Override
    public void onParsingErrors(List<PHHueParsingError> list)
    {
        Log.d(TAG, "Hue has parsing errors: " + Integer.toString(list.size()) + " problems");
    }

    public boolean isBridgeConnected()
    {
        return this.phHueSDK.getSelectedBridge() != null;
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
}