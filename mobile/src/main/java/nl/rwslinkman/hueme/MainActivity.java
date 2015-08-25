package nl.rwslinkman.hueme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nl.rwslinkman.hueme.fragments.GroupsFragment;
import nl.rwslinkman.hueme.fragments.InfoFragment;
import nl.rwslinkman.hueme.fragments.LightsFragment;
import nl.rwslinkman.hueme.navigation.NavigationDrawerCallbacks;
import nl.rwslinkman.hueme.service.HueService;
import nl.rwslinkman.hueme.service.HueServiceStateListener;
import nl.rwslinkman.hueme.ui.MainActivityView;

/**
 * @author Rick Slinkman
 */
public class MainActivity extends AppCompatActivity implements HueServiceStateListener
{
    public static final String TAG = MainActivity.class.getSimpleName();

    private final BroadcastReceiver hueUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            switch (action) {
                case HueService.DISPLAY_NO_BRIDGE_STATE:
                    // TODO: Display "NoBridgeFragment"
                    Log.d(TAG, "No bridge found, received via broadcast");
                    break;
                case HueService.HUE_HEARTBEAT_UPDATE: {
                    Log.d(TAG, "Heartbeat in activity");
                    HueService service = app.getHueService();
                    service.unregisterReceiver(this);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mView.displayConnectedState();
                        }
                    });
                    break;
                }
                case HueService.HUE_AP_NOTRESPONDING: {
                    HueService service = app.getHueService();
                    mView.displayNoBridgeState(service.getCurrentServiceState() == HueService.STATE_SCANNING);
                    break;
                }
            }
        }
    };
    private HueMe app;
    private MainActivityView mView;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "Activity created");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        this.mView = new MainActivityView(this);
        this.mView.create();

        mView.displayLoadingState();
    }

    @Override
    protected void onResume()
    {
        Log.d(TAG, "Activity resumed");
        super.onResume();

        app = (HueMe) getApplication();
        if(!app.isServiceReady())
        {
            app.subscribeHueServiceState(this);
        }
        else
        {
            this.onHueServiceReady();
        }
    }

    @Override
    public void onHueServiceReady()
    {
        Log.d(TAG, "Hue service ready");
        HueService service = app.getHueService();
        service.registerReceiver(hueUpdateReceiver, this.getDisplayUpdatesFilter());

        Log.d(TAG, String.valueOf(service.getCurrentServiceState()));
        if(service.getCurrentServiceState() == HueService.STATE_CONNECTED)
        {
            mView.displayConnectedState();
        }
        else if(service.getCurrentServiceState() == HueService.STATE_SCANNING)
        {
            mView.displayNoBridgeState(false);
        }
        else if(service.getCurrentServiceState() == HueService.STATE_CONNECTING)
        {
            service.registerReceiver(hueUpdateReceiver, this.getConnectionUpdatesFilter());
            mView.displayConnectingState();
        }
    }

    @Override
    public void onHueServiceHalted()
    {
        Log.d(TAG, "Hue service no longer available");
    }

    private IntentFilter getDisplayUpdatesFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HueService.DISPLAY_NO_BRIDGE_STATE);
        intentFilter.addAction(HueService.HUE_HEARTBEAT_UPDATE);
        return intentFilter;
    }

    private IntentFilter getConnectionUpdatesFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HueService.HUE_AP_NOTRESPONDING);
        return intentFilter;
    }

    public MainActivityView getView()
    {
        return mView;
    }
}
