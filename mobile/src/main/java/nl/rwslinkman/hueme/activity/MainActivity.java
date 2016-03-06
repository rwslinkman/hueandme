package nl.rwslinkman.hueme.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHLight;

import nl.rwslinkman.hueme.HueMe;
import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.service.HueService;
import nl.rwslinkman.hueme.service.HueServiceStateListener;
import nl.rwslinkman.hueme.ui.MainActivityView;

/**
 * @author Rick Slinkman
 */
public class MainActivity extends AppCompatActivity implements HueServiceStateListener
{
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUESTCODE_DETAIL_GROUP = 1;
    private static final int REQUESTCODE_DETAIL_LIGHT = 2;

    private final BroadcastReceiver hueUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            switch (action) {
                case HueService.DISPLAY_NO_BRIDGE_STATE: {
                    // TODO: Display "NoBridgeFragment"
                    Log.wtf(TAG, "No bridge found, received via broadcast");
                    break;
                }
                case HueService.HUE_HEARTBEAT_UPDATE: {
                    Log.i(TAG, "Hue heartbeat in MainActivity");
                    HueService service = app.getHueService();
                    service.unregisterReceiver(this);

                    new Handler().post(new Runnable() {
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
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        this.mView = new MainActivityView(this);
        this.mView.create();
        this.mView.displayLoadingState();
    }

    @Override
    protected void onResume()
    {
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
    protected void onPause()
    {
        super.onPause();
        this.unregisterServiceReceiver();
    }

    public void unregisterServiceReceiver()
    {
        HueService service = app.getHueService();
        service.unregisterReceiver(hueUpdateReceiver);
    }

    public void registerServiceReceiver(IntentFilter updatesFilter)
    {
        HueService service = app.getHueService();
        service.registerReceiver(hueUpdateReceiver, updatesFilter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUESTCODE_DETAIL_GROUP || requestCode == REQUESTCODE_DETAIL_LIGHT)
        {
            this.registerServiceReceiver(getDisplayUpdatesFilter());
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        return mView.handleOptionsMenu(inflater, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return mView.onMenuItemClick(item);
    }

    @Override
    public void onHueServiceReady()
    {
        HueService service = app.getHueService();
        this.registerServiceReceiver(this.getDisplayUpdatesFilter());

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

    public IntentFilter getDisplayUpdatesFilter()
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

    public void startDetailActivity(PHGroup resource)
    {
        Intent detailIntent = new Intent(this, GroupDetailActivity.class);
        detailIntent.putExtra(GroupDetailActivity.EXTRA_GROUP_IDENTIFIER, resource.getIdentifier());

        app.getHueService().unregisterReceiver(hueUpdateReceiver);
        this.startActivityForResult(detailIntent, REQUESTCODE_DETAIL_GROUP);
    }

    public void startDetailActivity(PHLight resource)
    {
        Intent detailIntent = new Intent(this, LightDetailActivity.class);
        detailIntent.putExtra(LightDetailActivity.EXTRA_LIGHT_IDENTIFIER, resource.getIdentifier());

        app.getHueService().unregisterReceiver(hueUpdateReceiver);
        this.startActivityForResult(detailIntent, REQUESTCODE_DETAIL_LIGHT);
    }
}