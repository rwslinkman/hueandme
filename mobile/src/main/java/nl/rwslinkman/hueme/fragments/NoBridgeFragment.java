package nl.rwslinkman.hueme.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.util.ArrayList;

import nl.rwslinkman.awesome.TextAwesome;
import nl.rwslinkman.hueme.HueMe;
import nl.rwslinkman.hueme.MainActivity;
import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.service.HueService;
import nl.rwslinkman.hueme.ui.HueIPAddressAdapter;
import nl.rwslinkman.hueme.ui.MainActivityView;

public class NoBridgeFragment extends Fragment implements View.OnClickListener, HueIPAddressAdapter.OnConnectButtonListener
{
    public static final String TAG = NoBridgeFragment.class.getSimpleName();
    private final BroadcastReceiver hueUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            switch (action)
            {
                case HueService.HUE_AP_FOUND:
                    ArrayList<String> apList = intent.getStringArrayListExtra(HueService.INTENT_EXTRA_ACCESSPOINTS_IP);
                    onBridgeFound(apList);
                    break;
                case HueService.HUE_AP_REQUIRES_PUSHLINK:
                    String ipAddress = intent.getStringExtra(HueService.INTENT_EXTRA_PUSHLINK_IP);
                    onPushlinkRequired(ipAddress);
                    break;
                case HueService.HUE_AP_NOTRESPONDING:
                    onConnectionError();
                    break;
                case HueService.BRIDGE_CONNECTED:
                    onConnectionSuccess();
            }
        }
    };
    private boolean mIsScanningForBridges;
    private View mRootView;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int rootViewToInflate = (mIsScanningForBridges) ? R.layout.fragment_scanning : R.layout.fragment_nobridges;
        this.mRootView = inflater.inflate(rootViewToInflate, container, false);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

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
    }

    private void createNotScanningView()
    {
        mRootView.findViewById(R.id.nobridges_startscan_button).setOnClickListener(this);
    }

    private void createScanningView()
    {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.scanning_list_bridges);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // specify an adapter (see also next example)
        HueIPAddressAdapter mAdapter = new HueIPAddressAdapter(new ArrayList<String>(), this);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void onBridgeFound(final ArrayList<String> ipAddresses)
    {
        if(getActivity() == null)
        {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Insert new list of IP addresses
                HueIPAddressAdapter adapter = new HueIPAddressAdapter(ipAddresses, NoBridgeFragment.this);
                mRecyclerView.swapAdapter(adapter, true);

                CircularProgressView progressView = (CircularProgressView) mRootView.findViewById(R.id.scanning_spinner_view);
                progressView.setVisibility(View.INVISIBLE);

                TextAwesome warningView = (TextAwesome) mRootView.findViewById(R.id.scanning_warning_view);
                warningView.setVisibility(View.VISIBLE);
                warningView.setText(getString(R.string.fa_lightbulb_o));
                warningView.setTextColor(getResources().getColor(R.color.rwslinkman_blue_light));

                TextView messageView = (TextView) mRootView.findViewById(R.id.scanning_text_view);
                messageView.setText(getString(R.string.scanning_text_bridgesfound));
            }
        });
    }

    private void onPushlinkRequired(String ipAddress)
    {
        // Hide spinner view
        CircularProgressView progressView = (CircularProgressView) mRootView.findViewById(R.id.scanning_spinner_view);
        progressView.setVisibility(View.INVISIBLE);
        // Show warning icon
        TextAwesome warningView = (TextAwesome) mRootView.findViewById(R.id.scanning_warning_view);
        warningView.setVisibility(View.VISIBLE);

        // Change text to match state
        TextView scanningText = (TextView) mRootView.findViewById(R.id.scanning_text_view);
        scanningText.setText(getString(R.string.scanning_text_authrequired));

        HueService service = ((HueMe)getActivity().getApplication()).getHueService();
        service.startPushlink(ipAddress);

        Toast.makeText(getActivity(), getString(R.string.nobridges_instruction_presspushlink), Toast.LENGTH_SHORT).show();
    }

    private void onConnectionSuccess()
    {
        if(getActivity() == null)
        {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setVisibility(View.GONE);

                CircularProgressView progressView = (CircularProgressView) mRootView.findViewById(R.id.scanning_spinner_view);
                progressView.setVisibility(View.INVISIBLE);

                TextAwesome warningView = (TextAwesome) mRootView.findViewById(R.id.scanning_warning_view);
                warningView.setVisibility(View.VISIBLE);
                warningView.setText(getString(R.string.fa_lightbulb_o));
                warningView.setTextColor(getResources().getColor(R.color.rwslinkman_blue_dark));

                TextView messageView = (TextView) mRootView.findViewById(R.id.scanning_text_view);
                messageView.setText(getString(R.string.scanning_text_success));

                Button continueButton = (Button) mRootView.findViewById(R.id.scanning_btn_successcontinue);
                continueButton.setVisibility(View.VISIBLE);
                continueButton.setOnClickListener(NoBridgeFragment.this);
            }
        });
    }

    private void onConnectionError()
    {
        // TODO:
        Log.d(TAG, "onConnectionError");
    }

    private IntentFilter getScanningUpdatesFilters()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HueService.SCANNING_STARTED);
        intentFilter.addAction(HueService.HUE_AP_FOUND);
        intentFilter.addAction(HueService.HUE_AP_REQUIRES_PUSHLINK);
        intentFilter.addAction(HueService.BRIDGE_CONNECTED);
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
                service.startScanning();
            }
            activityView.displayNoBridgeState(true);
        }
        else if(v.getId() == R.id.scanning_btn_successcontinue)
        {
            MainActivityView activityView = ((MainActivity)getActivity()).getView();
            activityView.displayConnectedState();
        }
    }

    @Override
    public void onConnectClick(View connectButton, String ipAddress)
    {
        // Show spinner view
        CircularProgressView progressView = (CircularProgressView) mRootView.findViewById(R.id.scanning_spinner_view);
        progressView.setVisibility(View.VISIBLE);
        // Hide warning icon
        TextAwesome warningView = (TextAwesome) mRootView.findViewById(R.id.scanning_warning_view);
        warningView.setVisibility(View.GONE);
        // Change text to match state
        TextView scanningText = (TextView) mRootView.findViewById(R.id.scanning_text_view);
        scanningText.setText(getString(R.string.scanning_text_connecting));

        HueMe app = (HueMe) getActivity().getApplication();
        HueService service = app.getHueService();
        if(service.getCurrentServiceState() != HueService.STATE_SCANNING)
        {
            service.connectToAccessPoint(ipAddress);
        }
    }
}
