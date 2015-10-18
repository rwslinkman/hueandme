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
    public void createFragment(View rootView)
    {
        // No action
    }

    public static ConnectingFragment newInstance()
    {
        ConnectingFragment fragment = new ConnectingFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_FRAGMENT_MARKER, FragmentMarker.Connecting.getValue());
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(false);
        return fragment;
    }
}
