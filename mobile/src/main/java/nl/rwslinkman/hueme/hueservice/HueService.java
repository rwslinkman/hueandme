package nl.rwslinkman.hueme.hueservice;

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
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

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
    // Class variables
    private final IBinder mBinder = new LocalBinder();
    private PHHueSDK phHueSDK;
    private HueBroadcaster broadcaster;
    private int currentServiceState;

    @Override
    public IBinder onBind(Intent intent)
    {
        broadcaster = new HueBroadcaster(this);
        currentServiceState = HueService.STATE_IDLE;

        Log.d(TAG, "HueService bound to application");
        phHueSDK = PHHueSDK.getInstance();
        phHueSDK.setAppName(getString(R.string.app_name));

        PHNotificationManager phNotificationManager = phHueSDK.getNotificationManager();
        phNotificationManager.registerSDKListener(this);

        PHBridge phHueBridge = phHueSDK.getSelectedBridge();
        if(phHueBridge == null)
        {
            // No bridges found
            Log.d(TAG, "HueService starting bridge search");
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
    }

    public int getCurrentServiceState()
    {
        return this.currentServiceState;
    }

    @Override
    public void onCacheUpdated(List<Integer> list, PHBridge phBridge)
    {
        Log.d(TAG, "Hue bridge cache updated");
    }

    @Override
    public void onBridgeConnected(PHBridge phBridge)
    {
        Log.d(TAG, "Hue bridge connected");
        this.currentServiceState = HueService.STATE_CONNECTED;
    }

    @Override
    public void onAuthenticationRequired(PHAccessPoint phAccessPoint)
    {
        Log.d(TAG, "Hue bridge requires pushlink");
    }

    @Override
    public void onAccessPointsFound(List<PHAccessPoint> list)
    {
        Log.d(TAG, "Found Hue access points");
        for(PHAccessPoint ap : list)
        {
            Log.d(TAG, "Found Hue AP @ IP " + ap.getIpAddress());
        }
        this.currentServiceState = HueService.STATE_IDLE;
    }

    @Override
    public void onError(int errorCode, String errorMessage)
    {
        if(errorCode == PHMessageType.BRIDGE_NOT_FOUND)
        {
            // Broadcast to listeners
            broadcaster.setAction(HueBroadcaster.DISPLAY_NO_BRIDGE_STATE);
            broadcaster.broadcast();
        }
        Log.e(TAG, "Hue SDK error: " + errorMessage);
    }

    @Override
    public void onConnectionResumed(PHBridge phBridge)
    {
        Log.d(TAG, "Connection resumed with Hue bridge");
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