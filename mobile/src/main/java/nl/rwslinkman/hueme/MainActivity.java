package nl.rwslinkman.hueme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nl.rwslinkman.hueme.fragments.GroupsFragment;
import nl.rwslinkman.hueme.fragments.InfoFragment;
import nl.rwslinkman.hueme.fragments.LightsFragment;
import nl.rwslinkman.hueme.fragments.NavigationDrawerFragment;
import nl.rwslinkman.hueme.hueservice.HueBroadcaster;
import nl.rwslinkman.hueme.hueservice.HueService;
import nl.rwslinkman.hueme.hueservice.HueServiceStateListener;
import nl.rwslinkman.hueme.navigation.NavigationDrawerCallbacks;


public class MainActivity extends ActionBarActivity implements NavigationDrawerCallbacks, HueServiceStateListener
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
                Log.d(TAG, "No bridge found");
            }
            Log.d(TAG, "Broadcast received: " + action);
        }
    };
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    private List<Fragment> fragmentList;
    private HueMe app;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        fragmentList = new ArrayList<>();
        fragmentList.add(LightsFragment.newInstance());
        fragmentList.add(GroupsFragment.newInstance());
        fragmentList.add(InfoFragment.newInstance());

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar, fragmentList);
        // TODO: populate the navigation drawer
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
            // Subscribe to HueService for display events
            HueService hueService = app.getHueService();
            hueService.registerReceiver(hueUpdateReceiver, getDisplayUpdatesFilter());
            Log.d(TAG, "HueService obtained in MainActivity and registered to updates");
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position)
    {
        // update the main content by replacing fragments
        Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
        Fragment selectedFragment = fragmentList.get(position);
        Log.d(TAG, selectedFragment.getClass().getSimpleName());
    }


    @Override
    public void onBackPressed()
    {
        if (mNavigationDrawerFragment.isDrawerOpen())
        {
            mNavigationDrawerFragment.closeDrawer();
        }
        else
        {
            super.onBackPressed();
        }
    }


    @Override
    public void onHueServiceReady()
    {
        HueService service = app.getHueService();
        service.registerReceiver(hueUpdateReceiver, this.getDisplayUpdatesFilter());
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

    public List<Fragment> getChildFragments() {
        return fragmentList;
    }
}
