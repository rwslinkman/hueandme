package nl.rwslinkman.hueme.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.rwslinkman.hueme.ui.MainActivityView;

/**
 * Created by Rick on 27-9-2015.
 */
public abstract class AbstractFragment extends Fragment
{
    public enum FragmentMarker
    {
        Connecting(0),
        Groups(1),
        Lights(2),
        Info(3),
        Loading(4), NoBridge(5);
        private int value;

        FragmentMarker(int value) {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return Integer.toString(this.value);
        }
    }

    public abstract int getLayoutResource();
    public abstract FragmentMarker getFragmentMarker();
    public abstract void createFragment(View rootView);

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int resource = this.getLayoutResource();
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(resource, container, false);
        this.createFragment(rootView);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }
}
