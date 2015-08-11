package nl.rwslinkman.hueme.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.philips.lighting.hue.sdk.PHAccessPoint;

import java.util.ArrayList;

import nl.rwslinkman.hueme.HueMe;
import nl.rwslinkman.hueme.MainActivity;
import nl.rwslinkman.hueme.ui.MainActivityView;
import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.service.HueService;
import nl.rwslinkman.hueme.ui.HueIPAddressAdapter;

public class NoBridgeFragment extends Fragment implements View.OnClickListener, HueIPAddressAdapter.OnConnectButtonListener
{
    public static final String TAG = NoBridgeFragment.class.getSimpleName();
    private final BroadcastReceiver hueUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if(action.equals(HueService.HUE_AP_FOUND))
            {
                ArrayList<String> apList = intent.getStringArrayListExtra(HueService.INTENT_EXTRA_ACCESSPOINTS_IP);
                onBridgeFound(apList);
            }
        }
    };
    private boolean mIsScanningForBridges;
    private View mRootView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private HueIPAddressAdapter mAdapter;

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
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.scanning_list_bridges);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new HueIPAddressAdapter(new ArrayList<String>(), this);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void onBridgeFound(final ArrayList<String> ipAddresses)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Insert new list of IP addresses
                HueIPAddressAdapter adapter = new HueIPAddressAdapter(ipAddresses, NoBridgeFragment.this);
                mRecyclerView.swapAdapter(adapter, true);
            }
        });
    }

    private IntentFilter getScanningUpdatesFilters()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HueService.SCANNING_STARTED);
        intentFilter.addAction(HueService.HUE_AP_FOUND);
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
            HueService service = ((HueMe)getActivity().getApplication()).getHueService();
            if(service != null)
            {
                service.registerReceiver(hueUpdateReceiver, this.getScanningUpdatesFilters());
            }
            activityView.displayNoBridgeState(true);
        }
    }

    @Override
    public void onConnectClick(View connectButton, String ipAddress)
    {
        Toast.makeText(getActivity(), "Connect to " + ipAddress, Toast.LENGTH_SHORT).show();
    }
}
