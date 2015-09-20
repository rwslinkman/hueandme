package nl.rwslinkman.hueme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.philips.lighting.hue.listener.PHGroupListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLightState;

import java.util.List;
import java.util.Map;

import nl.rwslinkman.hueme.service.HueService;
import nl.rwslinkman.hueme.ui.GroupDetailActivityView;

public class GroupDetailActivity extends AppCompatActivity implements PHGroupListener
{
    public static final String TAG = GroupDetailActivity.class.getSimpleName();
    public static final String EXTRA_GROUP_IDENTIFIER = "nl.rwslinkman.hueme.groupdetailactivity.extra.group_identifier";
    private final BroadcastReceiver groupUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            switch (action)
            {
                case HueService.HUE_LIGHTS_STATE_UPDATE:
                case HueService.HUE_GROUPS_STATE_UPDATE:
                    String groupIdentifier = mActiveGroup.getIdentifier();
                    PHBridge bridge = mApp.getHueService().getBridge();
                    mActiveGroup = bridge.getResourceCache().getGroups().get(groupIdentifier);

                    PHLightState groupState = GroupDetailActivity.this.getGroupState();
                    mView.showGroupView(groupState);
                    break;
                default:
                    break;
            }
        }
    };
    private HueMe mApp;
    private GroupDetailActivityView mView;
    private PHGroup mActiveGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        this.mApp = (HueMe) getApplication();

        mView = new GroupDetailActivityView(this);

        Intent intent = getIntent();
        if (intent != null)
        {
            String groupIdentifier = intent.getStringExtra(GroupDetailActivity.EXTRA_GROUP_IDENTIFIER);

            PHBridge bridge = mApp.getHueService().getBridge();
            mActiveGroup = bridge.getResourceCache().getGroups().get(groupIdentifier);
            bridge.updateGroup(mActiveGroup, this);


        } else
        {
            Log.e(TAG, "Intent is null, no group received");
            Log.e(TAG, "Is there a group? " + Boolean.toString(this.mActiveGroup != null));
        }

        mView.createView(mActiveGroup);
    }

    public PHLightState getGroupState()
    {
        HueService service = mApp.getHueService();
        PHBridge bridge = service.getBridge();

        List<String> lightsInGroup = mActiveGroup.getLightIdentifiers();
        if (!lightsInGroup.isEmpty())
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

        // Register to updates
        HueService service = mApp.getHueService();
        service.registerReceiver(groupUpdateReceiver, this.getGroupUpdatesIntentFilter());

        this.mView.registerListeners();
    }

    public IntentFilter getGroupUpdatesIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HueService.HUE_HEARTBEAT_UPDATE);
        intentFilter.addAction(HueService.HUE_GROUPS_STATE_UPDATE);
        intentFilter.addAction(HueService.HUE_LIGHTS_STATE_UPDATE);
        return intentFilter;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        HueService service = mApp.getHueService();
        service.unregisterReceiver(groupUpdateReceiver);
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
        PHLightState updatedGroupState = this.getGroupState();
        this.mView.showGroupView(updatedGroupState);
    }

    @Override
    public void onError(int i, String s)
    {
        Log.d(TAG, "PHGroup.onError: " + s);
    }

    @Override
    public void onStateUpdate(Map<String, String> successAttribute, List<PHHueError> errorAttribute)
    {
        for(Map.Entry<String, String> entry : successAttribute.entrySet())
        {
            // TODO:
            // key = changed parameter
            // key = new value
        }

        for(PHHueError error : errorAttribute)
        {
            // TODO: ?
        }
    }

    public void updateGroupState(PHLightState state)
    {
        // Obtain service
        HueService service = mApp.getHueService();
        // Perform magic
        PHBridge bridge = service.getBridge();
        bridge.setLightStateForGroup(mActiveGroup.getIdentifier(), state, this);
    }
}
