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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.rwslinkman.hueme.activity.MainActivity;
import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.service.HueService;
import nl.rwslinkman.hueme.ui.BridgeResourceSwitchAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class LightsFragment extends AbstractActionMenuFragment implements BridgeResourceSwitchAdapter.OnBridgeResourceItemEventListener<PHLight>
{
    public static final String TAG = LightsFragment.class.getSimpleName();
    private RecyclerView mLightsListView;
    private BroadcastReceiver lightsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            switch(intent.getAction())
            {
                case HueService.HUE_LIGHTS_STATE_UPDATE:
                    Log.d(TAG, "Lights state updated");
                    break;
                case HueService.HUE_POSSIBLE_STATE_UPDATE:
                    Log.d(TAG, "Possible state update");
                    break;
            }
        }
    };

    public static LightsFragment newInstance()
    {
        LightsFragment fragment = new LightsFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_FRAGMENT_MARKER, FragmentMarker.Lights.getValue());
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(this.mActiveBridge != null)
        {
            // Prepare data
            List<PHLight> hueLights = this.mActiveBridge.getResourceCache().getAllLights();
            String noLightsText = this.getString(R.string.lights_nolights_text);

            Map<PHLight, Boolean> hueStateLights = new HashMap<>();
            for(PHLight light : hueLights)
            {
                PHLightState lightState = light.getLastKnownLightState();
                hueStateLights.put(light, lightState.isOn());
            }

            // Prepare adapter
            BridgeResourceSwitchAdapter<PHLight> lightsAdapter = new BridgeResourceSwitchAdapter<>(this.getResources(), hueStateLights, noLightsText);
            lightsAdapter.setOnBridgeResourceItemEventListener(this);

            // Insert adapter
            mLightsListView.setHasFixedSize(true);
            mLightsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mLightsListView.setAdapter(lightsAdapter);
        }
        else
        {
            Log.e(TAG, "No bridge set :(");
        }

        this.registerUpdates(this.lightsReceiver, this.getLightUpdatesIntentFilter());
    }

    @Override
    public int getMenuResource()
    {
        return R.menu.toolbarmenu_lights;
    }

    @Override
    public boolean handleMenuItemClick(MenuItem item)
    {
        if(item.getItemId() == R.id.lights_add_action)
        {
            Toast.makeText(this.getActivity(), "Search new lights", Toast.LENGTH_SHORT).show();
            ((MainActivity)getActivity()).getView().displaySearchLights();
            return true;
        }
        return false;
    }

    @Override
    public int getLayoutResource()
    {
        return R.layout.fragment_lights;
    }

    @Override
    public void createFragment(View rootView)
    {
        this.mLightsListView = (RecyclerView) rootView.findViewById(R.id.lights_list_view);
    }

    @Override
    public void onBridgeResourceItemClicked(PHLight clickedItem)
    {
        ((MainActivity) getActivity()).startDetailActivity(clickedItem);
    }

    @Override
    public void onBridgeResourceItemSwitchChanged(PHLight clickedItem, boolean isChecked)
    {
        if(this.mActiveBridge != null)
        {
            PHLightState state = new PHLightState();
            state.setOn(isChecked);

            this.mActiveBridge.updateLightState(clickedItem, state);
        }
        else
        {
            Log.e(TAG, "No bridge set :(");
        }
    }

    public IntentFilter getLightUpdatesIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HueService.HUE_LIGHTS_STATE_UPDATE);
        intentFilter.addAction(HueService.HUE_POSSIBLE_STATE_UPDATE);
        return intentFilter;
    }
}
