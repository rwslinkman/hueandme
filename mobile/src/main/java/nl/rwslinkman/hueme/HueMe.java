package nl.rwslinkman.hueme;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * class HueMe
 * @author Rick Slinkman
 */
public class HueMe extends Application
{
    public static final String TAG = HueMe.class.getSimpleName();
    private HueService hueService;

    private ServiceConnection hueServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            Log.d(TAG, "HueService connected to HueMe");
            hueService = ((HueService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            Log.d(TAG, "HueService disconnected from HueMe");
        }
    };

    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.d(TAG, "HueMe binding service onCreate");
        bindService(new Intent(this, HueService.class), hueServiceConnection, BIND_AUTO_CREATE);
    }

    public HueService getHueService()
    {
        return hueService;
    }
}
