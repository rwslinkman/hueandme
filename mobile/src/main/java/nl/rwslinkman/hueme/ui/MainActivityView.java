package nl.rwslinkman.hueme.ui;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.philips.lighting.model.PHBridge;

import nl.rwslinkman.awesome.DrawableAwesome;
import nl.rwslinkman.hueme.HueMe;
import nl.rwslinkman.hueme.MainActivity;
import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.fragments.AbstractActionMenuFragment;
import nl.rwslinkman.hueme.fragments.AbstractFragment;
import nl.rwslinkman.hueme.fragments.AddGroupFragment;
import nl.rwslinkman.hueme.fragments.ConnectingFragment;
import nl.rwslinkman.hueme.fragments.GroupsFragment;
import nl.rwslinkman.hueme.fragments.InfoFragment;
import nl.rwslinkman.hueme.fragments.LightsFragment;
import nl.rwslinkman.hueme.fragments.LoadingFragment;
import nl.rwslinkman.hueme.fragments.NoBridgeFragment;
import nl.rwslinkman.hueme.service.HueService;

public class MainActivityView implements NavigationView.OnNavigationItemSelectedListener, Toolbar.OnMenuItemClickListener
{
    public static final String TAG = MainActivityView.class.getSimpleName();
    private static final int NAVHEADER_STATE_BULB_SIZE = 15; // unit of measure unclear
    private MainActivity mActivity;
    private DrawerLayout mDrawerLayout;
    private FloatingActionButton mStateBulbView;
    private TextView mStateMessageView;
    private NavigationView mNavigationView;
    private AbstractFragment mCurrentFragment;
    private Toolbar mToolbar;

    public MainActivityView(MainActivity parent)
    {
        this.mActivity = parent;
    }

    public void create()
    {
        // Init Toolbar
        this.mToolbar = (Toolbar) mActivity.findViewById(R.id.mainactivity_toolbar_view);
        mToolbar.setOnMenuItemClickListener(this);
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

    public void displayConnectedState()
    {
        // Set Header state to "scanning"
        DrawableAwesome.DrawableAwesomeBuilder stateBulbBuilder = new DrawableAwesome.DrawableAwesomeBuilder(mActivity, R.string.fa_lightbulb_o);
        stateBulbBuilder.setSize(NAVHEADER_STATE_BULB_SIZE);
        stateBulbBuilder.setColor(mActivity.getResources().getColor(R.color.rwslinkman_blue_dark));
        mStateBulbView.setImageDrawable(stateBulbBuilder.build());

        mStateMessageView.setText(mActivity.getString(R.string.header_statemessage_connected));

        mNavigationView.getMenu().clear();
        mNavigationView.inflateMenu(R.menu.navmenu_default);
        mNavigationView.setNavigationItemSelectedListener(this);

        // TODO: Investigate if I should check the NavigationView to determine the next method
        this.displayGroups();
    }

    public void displayGroups()
    {
        HueService service = ((HueMe) mActivity.getApplication()).getHueService();
        PHBridge bridge = service.getBridge();

        GroupsFragment fragment = GroupsFragment.newInstance();
        fragment.setActiveBridge(bridge);
        this.switchFragment(fragment);
    }

    private void switchFragment(AbstractFragment fragmentToDisplay)
    {
        Log.d(TAG, "Switching to fragment " + fragmentToDisplay.getClass().getSimpleName());
        // update the main content by replacing fragments
        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, fragmentToDisplay)
                .commit();

        if(mDrawerLayout.isDrawerOpen(mNavigationView))
        {
            mDrawerLayout.closeDrawer(mNavigationView);
        }
        this.mCurrentFragment = fragmentToDisplay;
        this.mToolbar.getMenu().clear();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem)
    {
        switch(menuItem.getItemId())
        {
            case R.id.navitem_groups:
                this.displayGroups();
                break;
            case R.id.navitem_info:
                this.displayInfo();
                break;
            case R.id.navitem_lights:
                this.displayLights();
        }

        // Set the menu correctly
        menuItem.setCheckable(true);
        menuItem.setChecked(true);
        return false;
    }

    private void displayInfo()
    {
        InfoFragment fragment = InfoFragment.newInstance();
        this.switchFragment(fragment);
    }

    private void displayLights()
    {
        LightsFragment fragment = LightsFragment.newInstance();
        this.switchFragment(fragment);
    }

    public void displayConnectingState()
    {
        ConnectingFragment fragment = ConnectingFragment.newInstance();
        this.switchFragment(fragment);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        if(this.mCurrentFragment.hasOptionsMenu())
        {
            return ((AbstractActionMenuFragment) this.mCurrentFragment).handleMenuItemClick(item);
        }
        return false;
    }

    public boolean handleOptionsMenu(MenuInflater inflater, Menu menu)
    {
        if(!this.mCurrentFragment.hasOptionsMenu())
        {
            return false;
        }

        int menuRes = ((AbstractActionMenuFragment) this.mCurrentFragment).getMenuResource();
        inflater.inflate(menuRes, menu);
        return true;
    }

    public void displayAddGroup()
    {
        this.mActivity.unregisterServiceReceiver();

        HueService service = ((HueMe) mActivity.getApplication()).getHueService();
        PHBridge bridge = service.getBridge();

        AddGroupFragment fragment = AddGroupFragment.newInstance();
        fragment.setActiveBridge(bridge);
        this.switchFragment(fragment);
    }
}
