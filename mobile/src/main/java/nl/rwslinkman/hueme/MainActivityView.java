package nl.rwslinkman.hueme;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import nl.rwslinkman.awesome.DrawableAwesome;
import nl.rwslinkman.hueme.fragments.LoadingFragment;
import nl.rwslinkman.hueme.fragments.NoBridgeFragment;

public class MainActivityView implements NavigationView.OnNavigationItemSelectedListener
{
    public static final String TAG = MainActivityView.class.getSimpleName();
    private static final int NAVHEADER_STATE_BULB_SIZE = 15; // unit of measure unclear
    private MainActivity mActivity;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private FloatingActionButton mStateBulbView;
    private TextView mStateMessageView;
    private Menu mNavigationMenu;
    private NavigationView mNavigationView;
    public boolean overrideScanning;

    public MainActivityView(MainActivity parent)
    {
        this.mActivity = parent;
        overrideScanning = true;
    }

    public void create()
    {
        // Init Toolbar
        mToolbar = (Toolbar) mActivity.findViewById(R.id.toolbar_actionbar);
        mActivity.setSupportActionBar(mToolbar);

        // Init NavigationDrawer
        mDrawerLayout = (DrawerLayout) mActivity.findViewById(R.id.drawer);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                mActivity,  mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActivity.getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        // Init NavigationDrawer
        mNavigationView = (NavigationView) mActivity.findViewById(R.id.navigation);
        mNavigationView.setNavigationItemSelectedListener(this);

        // Init NavigationDrawer header elements
        mStateBulbView = (FloatingActionButton) mActivity.findViewById(R.id.drawer_head_statebulb);
        mStateMessageView = (TextView) mActivity.findViewById(R.id.drawer_head_statemsg);
    }

    public void displayNoBridgeState(boolean isScanning)
    {
        if(this.overrideScanning)
        {
            isScanning = false;
        }
        int stateMessage = (isScanning) ? R.string.navheader_state_searching_for_bridges : R.string.navheader_state_no_bridges_connected;
        int stateColor = (isScanning) ? R.color.navheader_statebulb_disabled : R.color.android_red;

        // Set Header state to "scanning"
        DrawableAwesome.DrawableAwesomeBuilder stateBulbBuilder = new DrawableAwesome.DrawableAwesomeBuilder(mActivity, R.string.fa_lightbulb_o);
        stateBulbBuilder.setSize(NAVHEADER_STATE_BULB_SIZE);
        stateBulbBuilder.setColor(mActivity.getResources().getColor(stateColor));
        mStateBulbView.setImageDrawable(stateBulbBuilder.build());

        mStateMessageView.setText(mActivity.getString(stateMessage));

        mNavigationView.getMenu().clear();
        mNavigationView.inflateMenu(R.menu.navmenu_nobridges);

        // Switch to LoadingFragment (main view)
        NoBridgeFragment noBridgeFragment = NoBridgeFragment.newInstance();
        noBridgeFragment.setScanningMode(isScanning);
        this.switchFragment(noBridgeFragment);
    }

    public void displayLoadingState()
    {
        // Set header state to "loading"
        DrawableAwesome.DrawableAwesomeBuilder stateBulbBuilder = new DrawableAwesome.DrawableAwesomeBuilder(mActivity, R.string.fa_lightbulb_o);
        stateBulbBuilder.setSize(NAVHEADER_STATE_BULB_SIZE);
        stateBulbBuilder.setColor(mActivity.getResources().getColor(R.color.navheader_statebulb_disabled));
        mStateBulbView.setImageDrawable(stateBulbBuilder.build());
        mStateMessageView.setText(mActivity.getString(R.string.philips_hue_loading));

        mNavigationView.getMenu().clear();
        mNavigationView.inflateMenu(R.menu.navmenu_loading);

        // Switch to LoadingFragment (main view)
        this.switchFragment(LoadingFragment.newInstance());
    }

    private void switchFragment(Fragment fragmentToDisplay)
    {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, fragmentToDisplay)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem)
    {
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.mNavigationMenu = menu;
        return true; // false = hide, true = display
    }
}
