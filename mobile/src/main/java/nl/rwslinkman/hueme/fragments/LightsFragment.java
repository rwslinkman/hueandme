package nl.rwslinkman.hueme.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.rwslinkman.hueme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LightsFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lights, container, false);
    }

    public static LightsFragment newInstance()
    {
        LightsFragment fragment = new LightsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
}
