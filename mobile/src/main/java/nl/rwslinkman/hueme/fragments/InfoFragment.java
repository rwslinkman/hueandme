package nl.rwslinkman.hueme.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.ui.MainActivityView;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends AbstractFragment
{
    @Override
    public int getLayoutResource()
    {
        return R.layout.fragment_info;
    }

    @Override
    public FragmentMarker getFragmentMarker()
    {
        return FragmentMarker.Info;
    }

    @Override
    public void createFragment(View rootView)
    {
        //
    }

    public static InfoFragment newInstance()
    {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(false);
        return fragment;
    }
}
