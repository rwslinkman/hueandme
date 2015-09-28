package nl.rwslinkman.hueme.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.lighting.model.PHBridge;

import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.ui.MainActivityView;

/**
 * A simple {@link Fragment} subclass.
 */
public class LightsFragment extends AbstractFragment
{
    private PHBridge mActiveBridge;

    public static LightsFragment newInstance()
    {
        LightsFragment fragment = new LightsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    public void setActiveBridge(PHBridge activeBridge) {
        this.mActiveBridge = activeBridge;
    }

    @Override
    public int getLayoutResource()
    {
        return R.layout.fragment_lights;
    }

    @Override
    public FragmentMarker getFragmentMarker()
    {
        return FragmentMarker.Lights;
    }

    @Override
    public void createFragment(View rootView)
    {
        //
    }
}
