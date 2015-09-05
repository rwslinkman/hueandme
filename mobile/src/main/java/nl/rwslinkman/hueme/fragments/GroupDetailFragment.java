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
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.philips.lighting.hue.listener.PHGroupListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.List;
import java.util.Map;

import nl.rwslinkman.hueme.HueMe;
import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.service.HueService;

/**
 * @author Rick Slinkman
 */
public class GroupDetailFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, ColorPicker.OnColorSelectedListener {
    public static final String TAG = GroupDetailFragment.class.getSimpleName();
//    PHBridge.setLightStateForGroup

//    http://www.developers.meethue.com/documentation/color-conversions-rgb-xy
//    https://github.com/LarsWerkman/HoloColorPicker
    private final BroadcastReceiver groupUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            switch (action)
            {
                default:
                    break;
            }
        }
    };
    private PHGroup mActiveGroup;
    private RelativeLayout mRootView;
    private Switch mOnOffSwitch;
    private ColorPicker mColorPickerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        this.mRootView = (RelativeLayout) inflater.inflate(R.layout.fragment_groupdetail,
                container, false);

        TextView groupNameView = (TextView) this.mRootView.findViewById(R.id.groupdetail_group_name);
        groupNameView.setText(mActiveGroup.getName());

        this.mColorPickerView = (ColorPicker) this.mRootView.findViewById(R.id.groupdetail_colorpicker_view);

        this.mOnOffSwitch = (Switch) mRootView.findViewById(R.id.groupdetail_onoffswitch_view);


        return this.mRootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        HueMe app = (HueMe) this.getActivity().getApplication();
        HueService service = app.getHueService();

        service.registerReceiver(groupUpdateReceiver, this.getGroupUpdatesIntentFilter());

        PHBridge bridge = service.getBridge();
        mOnOffSwitch.setChecked(this.isGroupOn(bridge));

        // TODO: Subscribe to group status
        this.mOnOffSwitch.setOnCheckedChangeListener(this);
        this.mColorPickerView.setOnColorSelectedListener(this);
    }

    @Override
    public void onPause()
    {
        HueMe app = (HueMe) this.getActivity().getApplication();
        HueService service = app.getHueService();

        service.unregisterReceiver(groupUpdateReceiver);

        super.onPause();
    }

    public void setActiveGroup(PHGroup group)
    {
        this.mActiveGroup = group;
    }

    public static GroupDetailFragment newInstance()
    {
        GroupDetailFragment fragment = new GroupDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public IntentFilter getGroupUpdatesIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HueService.HUE_HEARTBEAT_UPDATE);
        intentFilter.addAction(HueService.HUE_GROUPSTATE_UPDATE);
        return intentFilter;
    }

    private boolean isGroupOn(PHBridge bridge)
    {
        boolean isOn = true;
        Map<String, PHLight> lights = bridge.getResourceCache().getLights();
        for(String str : mActiveGroup.getLightIdentifiers())
        {
            PHLight light = lights.get(str);
            PHLightState state = light.getLastKnownLightState();
            isOn |= state.isOn();
        }
        return isOn;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        String onState = (isChecked) ? "on" : "off";
        Log.d(TAG, "Group was turned " + onState);

        HueMe app = (HueMe) this.getActivity().getApplication();
        HueService service = app.getHueService();
        PHBridge bridge = service.getBridge();

        PHLightState state = new PHLightState();
        state.setOn(isChecked);
        bridge.setLightStateForGroup(this.mActiveGroup.getIdentifier(), state, new PHGroupListener() {
            @Override
            public void onCreated(PHGroup phGroup)
            {
                Log.d(TAG, "PHGroup.onCreated: " + phGroup.getIdentifier());
            }

            @Override
            public void onReceivingGroupDetails(PHGroup phGroup)
            {
                Log.d(TAG, "PHGroup.onReceivingGroupDetails: " + phGroup.getIdentifier());
            }
            @Override
            public void onReceivingAllGroups(List<PHBridgeResource> list)
            {
                Log.d(TAG, "PHGroup.onReceivingAllGroups");
            }

            @Override
            public void onSuccess()
            {
                Log.d(TAG, "PHGroup.onSuccess");
            }

            @Override
            public void onError(int i, String s)
            {
                Log.d(TAG, "PHGroup.onError: " + s);
            }

            @Override
            public void onStateUpdate(Map<String, String> map, List<PHHueError> list)
            {
                Log.d(TAG, "onStateUpdate");
            }
        });
    }

    @Override
    public void onColorSelected(int selectedColor)
    {
        Log.d(TAG, "Color selected " + Integer.toString(selectedColor));
    }
}
