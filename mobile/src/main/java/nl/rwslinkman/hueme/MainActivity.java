package nl.rwslinkman.hueme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import nl.rwslinkman.hueme.fragments.GroupsFragment;
import nl.rwslinkman.hueme.fragments.InfoFragment;
import nl.rwslinkman.hueme.fragments.LightsFragment;
import nl.rwslinkman.hueme.fragments.NavigationDrawerFragment;
import nl.rwslinkman.hueme.hueservice.HueBroadcaster;
import nl.rwslinkman.hueme.hueservice.HueService;
import nl.rwslinkman.hueme.hueservice.HueServiceStateListener;

/**
 * class MainActivity
 * @author Rick Slinkman
 */
public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, HueServiceStateListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private List<Fragment> fragmentList;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private final BroadcastReceiver hueUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Log.d(TAG, "Broadcast received: " + action);
        }
    };
    private HueMe app;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentList = new ArrayList<>();
        fragmentList.add(LightsFragment.newInstance());
        fragmentList.add(GroupsFragment.newInstance());
        fragmentList.add(InfoFragment.newInstance());

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
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
    protected void onPause()
    {
        // No longer receive updates from HueService
        unregisterReceiver(hueUpdateReceiver);
        super.onPause();
    }

    private IntentFilter getDisplayUpdatesFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HueBroadcaster.DISPLAY_NO_BRIDGE_STATE);
        return intentFilter;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position)
    {
        Fragment chosenFragment;
        if(fragmentList == null)
        {
            chosenFragment = LightsFragment.newInstance();
        }
        else {
            chosenFragment = fragmentList.get(position);
            if (chosenFragment == null) {
                return;
            }
        }

        this.switchFragment(chosenFragment);
    }

    private void switchFragment(Fragment fragmentToDisplay)
    {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, fragmentToDisplay)
                .commit();
    }

    public void restoreActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    }
}
