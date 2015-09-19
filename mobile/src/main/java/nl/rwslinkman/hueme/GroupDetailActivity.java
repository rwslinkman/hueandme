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
import android.widget.Switch;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.philips.lighting.hue.listener.PHGroupListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLightState;

import java.util.List;
import java.util.Map;

import nl.rwslinkman.hueme.helper.PhilipsHSB;
import nl.rwslinkman.hueme.helper.HueColorConverter;
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
    private HueMe mApp;
    private Switch mOnOffSwitch;
    private ColorPicker mColorPickerView;
    private PHGroup mActiveGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        this.mApp = (HueMe) getApplication();

        // Init views
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.groupdetail_toolbar_view);
        this.mColorPickerView = (ColorPicker) this.findViewById(R.id.groupdetail_colorpicker_view);
        this.mOnOffSwitch = (Switch) this.findViewById(R.id.groupdetail_onoffswitch_view);

        // Define active group
        Intent intent = getIntent();
        if(intent != null)
        {
            String groupIdentifier = intent.getStringExtra(GroupDetailActivity.EXTRA_GROUP_IDENTIFIER);

            PHBridge bridge = mApp.getHueService().getBridge();
            mActiveGroup = bridge.getResourceCache().getGroups().get(groupIdentifier);
            bridge.updateGroup(mActiveGroup, this);

            toolbar.setTitle(mActiveGroup.getName());

            PHLightState groupState = this.getRepLightState();
            showGroupView(groupState);
        }
        else
        {
            Log.e(TAG, "Intent is null, no group received");
            Log.e(TAG, "Is there a group? " + Boolean.toString(this.mActiveGroup != null));
        }

        // Set Toolbar to be ActionBar
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void showGroupView(final PHLightState state)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                int color = HueColorConverter.convertStateToColor(state);

                mOnOffSwitch.setChecked(state.isOn());
                mColorPickerView.setColor(color);
                mColorPickerView.setOldCenterColor(color);
                mColorPickerView.setNewCenterColor(color);
            }
        });
    }

    private PHLightState getRepLightState()
    {
        HueService service = mApp.getHueService();
        PHBridge bridge = service.getBridge();

        List<String> lightsInGroup = mActiveGroup.getLightIdentifiers();
        if(!lightsInGroup.isEmpty())
        {
            String repLightID = lightsInGroup.get(0);
            return bridge.getResourceCache().getLights().get(repLightID).getLastKnownLightState();
        }
        return null;
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        HueService service = mApp.getHueService();

        service.registerReceiver(groupUpdateReceiver, this.getGroupUpdatesIntentFilter());

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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        // Obtain service
        HueService service = mApp.getHueService();

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
        super.onPause();
        HueService service = mApp.getHueService();
        service.unregisterReceiver(groupUpdateReceiver);
    }

    @Override
    public void onColorSelected(int selectedColor)
    {
        PhilipsHSB color = HueColorConverter.convertColorToHSB(selectedColor);

        // Obtain service
        HueService service = mApp.getHueService();

        // Create light state
        PHLightState state = new PHLightState();
        state.setOn(true);
        state.setHue(color.getHue());
        state.setBrightness(color.getBrightness());
        state.setSaturation(color.getSaturation());

        // Perform magic
        PHBridge bridge = service.getBridge();
        bridge.setLightStateForGroup(mActiveGroup.getIdentifier(), state, this);
    }

    @Override
    public void onCreated(PHGroup phGroup)
    {
        Log.e(TAG, "PHGroup.onCreated: " + phGroup.getIdentifier());
    }

    @Override
    public void onReceivingGroupDetails(PHGroup phGroup)
    {
        Log.e(TAG, "PHGroup.onReceivingGroupDetails: " + phGroup.getIdentifier());
    }

    @Override
    public void onReceivingAllGroups(List<PHBridgeResource> list)
    {
        Log.e(TAG, "PHGroup.onReceivingAllGroups");
    }

    @Override
    public void onSuccess()
    {
        Log.e(TAG, "PHGroup.onSuccess");

        // Update mActiveGruop
        Map<String, PHGroup> groups = mApp.getHueService().getBridge().getResourceCache().getGroups();
        mActiveGroup = groups.get(mActiveGroup.getIdentifier());

        // Show new state
        PHLightState updatedGroupState = this.getRepLightState();
        this.showGroupView(updatedGroupState);
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
