package nl.rwslinkman.hueme;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;

/**
 * HueService
 * Keeps up with the Hue SDK instance
 * @author Rick Slinkman
 */
public class HueService extends Service
{
    public static final String TAG = HueService.class.getSimpleName();
    private final IBinder mBinder = new LocalBinder();
    private PHHueSDK phHueSDK;

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d(TAG, "HueService bound to application");
        phHueSDK = PHHueSDK.getInstance();

        PHBridge phHueBridge = phHueSDK.getSelectedBridge();
        Log.d(TAG, Boolean.toString(phHueBridge == null));
        return mBinder;
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