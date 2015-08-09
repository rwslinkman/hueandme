package nl.rwslinkman.hueme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nl.rwslinkman.hueme.fragments.GroupsFragment;
import nl.rwslinkman.hueme.fragments.InfoFragment;
import nl.rwslinkman.hueme.fragments.LightsFragment;
import nl.rwslinkman.hueme.hueservice.HueBroadcaster;
import nl.rwslinkman.hueme.hueservice.HueService;
import nl.rwslinkman.hueme.hueservice.HueServiceStateListener;
import nl.rwslinkman.hueme.navigation.NavigationDrawerCallbacks;


public class MainActivity extends AppCompatActivity implements NavigationDrawerCallbacks, HueServiceStateListener
{
    public static final String TAG = MainActivity.class.getSimpleName();

    private final BroadcastReceiver hueUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if(action.equals(HueBroadcaster.DISPLAY_NO_BRIDGE_STATE))
            {
                // TODO: Display "NoBridgeFragment"
                Log.d(TAG, "No bridge found, received via broadcast");
                return;
            }
            Log.d(TAG, "Broadcast received: " + action);
        }
    };

    private List<Fragment> mFragmentsList;
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

        // Init menu
        mFragmentsList = new ArrayList<>();
        mFragmentsList.add(LightsFragment.newInstance());
        mFragmentsList.add(GroupsFragment.newInstance());
        mFragmentsList.add(InfoFragment.newInstance());

        mView.displayLoadingState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        return mView.onCreateOptionsMenu(menu);
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
    public void onNavigationDrawerItemSelected(int position)
    {
        // update the main content by replacing fragments
        Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
        Fragment selectedFragment = mFragmentsList.get(position);
        Log.d(TAG, selectedFragment.getClass().getSimpleName());
    }

    @Override
    public void onHueServiceReady()
    {
        HueService service = app.getHueService();
        service.registerReceiver(hueUpdateReceiver, this.getDisplayUpdatesFilter());

        if(!service.isBridgeConnected())
        {
            mView.displayNoBridgeState(service.getCurrentServiceState() == HueService.STATE_SCANNING);
        }
        else
        {
            // TODO: Display "bridge connected" state
            Log.d(TAG, "Bridge is connected, display in MainActivityView");
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
        intentFilter.addAction(HueBroadcaster.DISPLAY_NO_BRIDGE_STATE);
        return intentFilter;
    }
}
