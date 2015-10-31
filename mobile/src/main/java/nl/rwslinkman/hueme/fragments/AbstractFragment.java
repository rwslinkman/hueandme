package nl.rwslinkman.hueme.fragments;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.philips.lighting.model.PHBridge;

import nl.rwslinkman.hueme.HueMe;
import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.service.HueService;

/**
 * @author Rick Slinkman
 */
public abstract class AbstractFragment extends Fragment
{
    public enum FragmentMarker
    {
        Connecting(0),
        Groups(1),
        Lights(2),
        Info(3),
        Loading(4),
        NoBridge(5),
        AddGroup(6);
        private int value;

        FragmentMarker(int value) {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return Integer.toString(this.value);
        }

        public int getValue()
        {
            return value;
        }
    }

    public static final String EXTRA_FRAGMENT_MARKER = AbstractFragment.class.getName() + "extra_fragment_marker";
    protected PHBridge mActiveBridge;

    public abstract int getLayoutResource();
    public abstract void createFragment(View rootView);

    public final void setActiveBridge(PHBridge activeBridge)
    {
        this.mActiveBridge = activeBridge;
    }

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

    protected void registerUpdates(BroadcastReceiver receiver, IntentFilter requestedUpdates)
    {
        HueMe app = (HueMe) getActivity().getApplication();
        HueService service = app.getHueService();
        service.registerReceiver(receiver, requestedUpdates);
    }

    protected void unregisterUpdates(BroadcastReceiver receiver)
    {
        HueMe app = (HueMe) getActivity().getApplication();
        HueService service = app.getHueService();
        service.unregisterReceiver(receiver);
    }

}
