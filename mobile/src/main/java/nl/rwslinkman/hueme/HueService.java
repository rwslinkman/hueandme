package nl.rwslinkman.hueme;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * HueService
 * Keeps up with the Hue SDK instance
 * @author Rick Slinkman
 */
public class HueService extends Service
{
    public static final String TAG = HueService.class.getSimpleName();
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d(TAG, "HueService bound to application");
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