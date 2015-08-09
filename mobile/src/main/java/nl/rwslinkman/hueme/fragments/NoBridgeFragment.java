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

import nl.rwslinkman.hueme.HueMe;
import nl.rwslinkman.hueme.MainActivity;
import nl.rwslinkman.hueme.MainActivityView;
import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.hueservice.HueBroadcaster;
import nl.rwslinkman.hueme.hueservice.HueService;

public class NoBridgeFragment extends Fragment implements View.OnClickListener
{
    public static final String TAG = NoBridgeFragment.class.getSimpleName();
    private final BroadcastReceiver hueUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Log.d(TAG, "Broadcast received: " + action);
        }
    };
    private boolean mIsScanningForBridges;
    private View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int rootViewToInflate = (mIsScanningForBridges) ? R.layout.fragment_scanning : R.layout.fragment_nobridges;
        this.mRootView = inflater.inflate(rootViewToInflate, container, false);

        if(mIsScanningForBridges)
        {
            HueService service = ((HueMe)getActivity().getApplication()).getHueService();
            if(service != null)
            {
                service.registerReceiver(hueUpdateReceiver, this.getScanningUpdatesFilters());
            }
            this.createScanningView();
        }
        else
        {
            this.createNotScanningView();
        }
        return mRootView;
    }

    private void createNotScanningView()
    {
        mRootView.findViewById(R.id.nobridges_startscan_button).setOnClickListener(this);
    }

    private void createScanningView()
    {
        Log.d(TAG, "Load spinner and listview to indicate search");
    }

    private IntentFilter getScanningUpdatesFilters()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HueBroadcaster.SCANNING_STARTED);
        intentFilter.addAction(HueBroadcaster.HUE_AP_FOUND);
        return intentFilter;
    }

    public void setScanningMode(boolean isScanning)
    {
        this.mIsScanningForBridges = isScanning;
    }

    public static NoBridgeFragment newInstance()
    {
        NoBridgeFragment fragment = new NoBridgeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.nobridges_startscan_button)
        {
            MainActivityView activityView = ((MainActivity)getActivity()).getView();
            activityView.overrideScanning = false;
            HueService service = ((HueMe)getActivity().getApplication()).getHueService();
            if(service != null)
            {
                service.registerReceiver(hueUpdateReceiver, this.getScanningUpdatesFilters());
            }
            activityView.displayNoBridgeState(true);
        }
    }
}