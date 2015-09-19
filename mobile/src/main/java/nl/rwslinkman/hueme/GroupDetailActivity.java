package nl.rwslinkman.hueme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

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

import nl.rwslinkman.hueme.service.HueService;

public class GroupDetailActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, ColorPicker.OnColorSelectedListener, PHGroupListener
{
    public static final String TAG = GroupDetailActivity.class.getSimpleName();
    public static final String EXTRA_GROUP_IDENTIFIER = "nl.rwslinkman.hueme.groupdetailactivity.extra.group_identifier";
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
    private Switch mOnOffSwitch;
    private ColorPicker mColorPickerView;
    private PHGroup mActiveGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        // Init Toolbar
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.groupdetail_toolbar_view);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);

        // Init other views
        this.mColorPickerView = (ColorPicker) this.findViewById(R.id.groupdetail_colorpicker_view);
        this.mOnOffSwitch = (Switch) this.findViewById(R.id.groupdetail_onoffswitch_view);

        // Define active group
        Intent intent = getIntent();
        if(intent != null)
        {
            String groupIdentifier = intent.getStringExtra(GroupDetailActivity.EXTRA_GROUP_IDENTIFIER);

            PHBridge bridge = ((HueMe) getApplication()).getHueService().getBridge();
            mActiveGroup = bridge.getResourceCache().getGroups().get(groupIdentifier);

            toolbar.setTitle(mActiveGroup.getName());

            showGroupView();
        }
    }

    private void showGroupView()
    {
        HueService service = ((HueMe) this.getApplication()).getHueService();
        PHBridge bridge = service.getBridge();


        List<String> lightsInGroup = mActiveGroup.getLightIdentifiers();
        if(!lightsInGroup.isEmpty())
        {
            String repLightID = lightsInGroup.get(0);
            PHLight repLight = bridge.getResourceCache().getLights().get(repLightID);

            PHLightState state = repLight.getLastKnownLightState();
            mOnOffSwitch.setChecked(state.isOn());
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        HueMe app = (HueMe) this.getApplication();
        HueService service = app.getHueService();

        service.registerReceiver(groupUpdateReceiver, this.getGroupUpdatesIntentFilter());

        PHBridge bridge = service.getBridge();
        mOnOffSwitch.setChecked(this.isGroupOn(bridge));

        // TODO: Subscribe to group status
        this.mOnOffSwitch.setOnCheckedChangeListener(this);
        this.mColorPickerView.setOnColorSelectedListener(this);
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
        // Debug info
        String onState = (isChecked) ? "on" : "off";
        Log.d(TAG, "Group was turned " + onState);

        // Obtain service
        HueMe app = (HueMe) this.getApplication();
        HueService service = app.getHueService();

        // Create light state
        PHLightState state = new PHLightState();
        state.setOn(isChecked);

        // Perform magic
        PHBridge bridge = service.getBridge();
        bridge.setLightStateForGroup(mActiveGroup.getIdentifier(), state, this);
    }

    @Override
    public void onPause()
    {
        HueMe app = (HueMe) this.getApplication();
        HueService service = app.getHueService();

        service.unregisterReceiver(groupUpdateReceiver);

        super.onPause();
    }

    @Override
    public void onColorSelected(int selectedColor)
    {
        Log.d(TAG, "Color selected " + Integer.toString(selectedColor));
    }

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
}
