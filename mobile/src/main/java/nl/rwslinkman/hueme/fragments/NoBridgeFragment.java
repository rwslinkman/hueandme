package nl.rwslinkman.hueme.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.hueservice.HueBroadcaster;

public class NoBridgeFragment extends Fragment
{
    public static final String TAG = NoBridgeFragment.class.getSimpleName();
    private final BroadcastReceiver hueUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if(action.equals(HueBroadcaster.DISPLAY_NO_BRIDGE_STATE))
            {
                Log.d(TAG, "No bridge found, received via broadcast");
                return;
            }
            Log.d(TAG, "Broadcast received: " + action);
        }
    };
    private boolean isScanningForBridges;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_nobridges, container, false);

        TextView scanningTextView = (TextView) rootView.findViewById(R.id.nobridges_scanningtext_view);
        String scanningText = (isScanningForBridges) ? "Scanning" : "Not scanning";
        scanningTextView.setText(scanningText);
        // TODO: Show startScan button if applicable
        // TODO: Hide button when scanning
        // TODO: Show list of found Bridge APs

        return rootView;
    }

    private IntentFilter getScanningUpdateFilters()
    {
        final IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(HueBroadcaster.SCANNING_UPDATE);
        return intentFilter;
    }

    public void setScanningMode(boolean isScanning)
    {
        this.isScanningForBridges = isScanning;
    }


    public static NoBridgeFragment newInstance()
    {
        NoBridgeFragment fragment = new NoBridgeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
}
