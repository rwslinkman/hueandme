package nl.rwslinkman.hueme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nl.rwslinkman.awesome.DrawableAwesome;
import nl.rwslinkman.hueme.fragments.GroupsFragment;
import nl.rwslinkman.hueme.fragments.InfoFragment;
import nl.rwslinkman.hueme.fragments.LightsFragment;
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
                Log.d(TAG, "No bridge found, received via broadcast");
            }
            Log.d(TAG, "Broadcast received: " + action);
        }
    };
    private Toolbar mToolbar;
    private List<Fragment> fragmentList;
    private HueMe app;
    private DrawerLayout mDrawerLayout;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        // Init NavigationDrawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,  mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        // Init menu
        fragmentList = new ArrayList<>();
        fragmentList.add(LightsFragment.newInstance());
        fragmentList.add(GroupsFragment.newInstance());
        fragmentList.add(InfoFragment.newInstance());

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.inflateMenu(R.menu.drawer);

        FloatingActionButton stateBulbView = (FloatingActionButton) findViewById(R.id.drawer_head_statebulb);
        DrawableAwesome.DrawableAwesomeBuilder stateBulbBuilder = new DrawableAwesome.DrawableAwesomeBuilder(this, R.string.fa_lightbulb_o);
        stateBulbBuilder.setColor(getResources().getColor(android.R.color.white));
        stateBulbBuilder.setSize(15);
        stateBulbView.setImageDrawable(stateBulbBuilder.build());

        // TODO: Display state "No bridges"
        Log.d(TAG, "stateBulb found: " + Boolean.toString(stateBulbView != null));

        switchFragment(fragmentList.get(1));

        // TODO: populate the navigation drawer
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

    @Override
    protected void onResume()
    {
        super.onResume();

        app = (HueMe) getApplication();
        if(!app.isServiceReady())
        {
            app.subscribeHueServiceState(this);
            this.displayLoadingState();
        }
        else
        {
            this.onHueServiceReady();
        }
    }

    private void displayLoadingState()
    {
        // TODO: Make MainActivity switch to LoadingFragment
        // TODO: Set header state to "loading"
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
