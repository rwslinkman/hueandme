package nl.rwslinkman.hueme;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import nl.rwslinkman.hueme.service.HueService;
import nl.rwslinkman.hueme.service.HueServiceStateListener;

/**
 * class HueMe
 * @author Rick Slinkman
 */
public class HueMe extends Application
{
    public static final String TAG = HueMe.class.getSimpleName();
    private HueService hueService;
    private List<HueServiceStateListener> subscribers;

    private ServiceConnection hueServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            Log.d(TAG, "Service connected");
            hueService = ((HueService.LocalBinder) service).getService();
            for(HueServiceStateListener subscriber : subscribers)
            {
                subscriber.onHueServiceReady();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            hueService = null;
            for(HueServiceStateListener subscriber : subscribers)
            {
                subscriber.onHueServiceHalted();
            }
        }
    };

    @Override
    public void onCreate()
    {
        super.onCreate();

        this.subscribers = new ArrayList<>();
        // Start HueService
        bindService(new Intent(this, HueService.class), hueServiceConnection, BIND_AUTO_CREATE);
    }

    public HueService getHueService()
    {
        return hueService;
    }

    public void subscribeHueServiceState(HueServiceStateListener listener)
    {
        this.subscribers.add(listener);
    }

    public void unsubscribeHueServiceState(HueServiceStateListener listener)
    {
        this.subscribers.remove(listener);
    }

    public boolean isServiceReady()
    {
        return this.getHueService() != null;
    }
}
