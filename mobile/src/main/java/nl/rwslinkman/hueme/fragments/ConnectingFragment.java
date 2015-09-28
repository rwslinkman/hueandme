package nl.rwslinkman.hueme.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.ui.MainActivityView;

public class ConnectingFragment extends AbstractFragment
{
    @Override
    public int getLayoutResource()
    {
        return R.layout.fragment_connecting;
    }

    @Override
    public FragmentMarker getFragmentMarker()
    {
        return FragmentMarker.Connecting;
    }

    @Override
    public void createFragment(View rootView)
    {
        //
    }

    public static ConnectingFragment newInstance()
    {
        ConnectingFragment fragment = new ConnectingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
}
