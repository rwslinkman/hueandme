package nl.rwslinkman.hueme.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.philips.lighting.model.PHBridge;

import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.ui.MainActivityView;

/**
 * A simple {@link Fragment} subclass.
 */
public class LightsFragment extends AbstractActionMenuFragment
{
    public static final String TAG = LightsFragment.class.getSimpleName();

    public static LightsFragment newInstance()
    {
        LightsFragment fragment = new LightsFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_FRAGMENT_MARKER, FragmentMarker.Lights.getValue());
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    @Override
    public int getMenuResource()
    {
        return R.menu.toolbarmenu_lights;
    }

    @Override
    public boolean handleMenuItemClick(MenuItem item)
    {
        if(item.getItemId() == R.id.lights_add_action)
        {
            Log.d(TAG, "Lights ADD clicked");
        }
        return false;
    }

    @Override
    public int getLayoutResource()
    {
        return R.layout.fragment_lights;
    }

    @Override
    public void createFragment(View rootView)
    {
        //
    }
}
