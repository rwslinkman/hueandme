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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nl.rwslinkman.awesome.DrawableAwesome;
import nl.rwslinkman.hueme.fragments.GroupsFragment;
import nl.rwslinkman.hueme.fragments.InfoFragment;
import nl.rwslinkman.hueme.fragments.LightsFragment;
import nl.rwslinkman.hueme.fragments.LoadingFragment;
import nl.rwslinkman.hueme.hueservice.HueBroadcaster;
import nl.rwslinkman.hueme.hueservice.HueService;
import nl.rwslinkman.hueme.hueservice.HueServiceStateListener;
import nl.rwslinkman.hueme.navigation.NavigationDrawerCallbacks;


public class MainActivity extends AppCompatActivity implements NavigationDrawerCallbacks, HueServiceStateListener, NavigationView.OnNavigationItemSelectedListener
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
    private Toolbar mToolbar;
    private List<Fragment> mFragmentsList;
    private HueMe app;
    private DrawerLayout mDrawerLayout;
    private FloatingActionButton mStateBulbView;
    private TextView mStateMessageView;
    private Menu mNavigationMenu;

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
        mFragmentsList = new ArrayList<>();
        mFragmentsList.add(LightsFragment.newInstance());
        mFragmentsList.add(GroupsFragment.newInstance());
        mFragmentsList.add(InfoFragment.newInstance());

        // Init NavigationDrawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.inflateMenu(R.menu.drawer);
        navigationView.setNavigationItemSelectedListener(this);

        // Init NavigationDrawer header elements
        mStateBulbView = (FloatingActionButton) findViewById(R.id.drawer_head_statebulb);
        mStateMessageView = (TextView) findViewById(R.id.drawer_head_statemsg);

        // TODO: populate the navigation drawer
        switchFragment(mFragmentsList.get(1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        this.mNavigationMenu = menu;
        return true; // false = hide, true = display
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

    private void displayScanningState()
    {
        // TODO: Make MainActivity switch to ScanningFragment
        // TODO: Set HEader state to "scanning"
//        mStateMessageView.setText("App is scanning");
        Log.d(TAG, "Show scanning state");
    }

    private void displayLoadingState()
    {
        int STATE_BULB_SIZE = 15; // unit of measure unclear

        // Set header state to "loading"
        DrawableAwesome.DrawableAwesomeBuilder stateBulbBuilder = new DrawableAwesome.DrawableAwesomeBuilder(this, R.string.fa_lightbulb_o);
        stateBulbBuilder.setSize(STATE_BULB_SIZE);
        stateBulbBuilder.setColor(getResources().getColor(android.R.color.white));
        mStateBulbView.setImageDrawable(stateBulbBuilder.build());
        mStateMessageView.setText(getString(R.string.philips_hue_loading));

        if(null != this.mNavigationMenu)
        {
            // Hide all menu items
            this.mNavigationMenu.findItem(R.id.navitem_groups).setVisible(false);
            this.mNavigationMenu.findItem(R.id.navitem_lights).setVisible(false);
            this.mNavigationMenu.findItem(R.id.navitem_info).setVisible(false);
            // Show item "starting"
            this.mNavigationMenu.findItem(R.id.navitem_starting).setVisible(true);
        }


        // Switch to LoadingFragment (main view)
        this.switchFragment(LoadingFragment.newInstance());
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
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

        switch(service.getCurrentServiceState())
        {
            case HueService.STATE_SCANNING:
                this.displayScanningState();
                break;
            // TODO: Undecided
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

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem)
    {
        // Return boolean tells if item must be selected after TODO action
        // TODO: Switch to selected item
        return true;
    }
}
