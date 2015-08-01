package nl.rwslinkman.hueme;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHNotificationManager;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;

import java.util.List;

/**
 * HueService
 * Keeps up with the Hue SDK instance
 * @author Rick Slinkman
 */
public class HueService extends Service implements PHSDKListener
{
    public static final String TAG = HueService.class.getSimpleName();
    private final IBinder mBinder = new LocalBinder();
    private PHHueSDK phHueSDK;

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d(TAG, "HueService bound to application");
        phHueSDK = PHHueSDK.getInstance();

        PHNotificationManager phNotificationManager = phHueSDK.getNotificationManager();
        phNotificationManager.registerSDKListener(this);

        PHBridgeSearchManager phSearchManager = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        phSearchManager.upnpSearch();
        phSearchManager.portalSearch();


        PHBridge phHueBridge = phHueSDK.getSelectedBridge();
        if(phHueBridge == null)
        {
            Log.d(TAG, "No Hue bridge connected");

        }

       // phHueBridge is null

        return mBinder;
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
    }

    @Override
    public void onError(int i, String s)
    {
        Log.e(TAG, s);
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

    /**
     * class LocalBinder
     */
    public class LocalBinder extends Binder
    {
        public HueService getService()
        {
            return HueService.this;
        }
    }
}