package nl.rwslinkman.hueme.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.philips.lighting.hue.listener.PHGroupListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLightState;

import java.util.List;
import java.util.Map;

import nl.rwslinkman.hueme.HueMe;
import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.service.HueService;
import nl.rwslinkman.hueme.ui.BridgeResourceDetailActivityView;

/**
 @* @author Rick Slinkman
 */
public class GroupDetailActivity extends BridgeResourceDetailActivity implements PHGroupListener {
    public static final String TAG = GroupDetailActivity.class.getSimpleName();
    public static final String EXTRA_GROUP_IDENTIFIER = "nl.rwslinkman.hueme.groupdetailactivity.extra.group_identifier";
    private static final int RESULTCODE_GROUP_DELETED = 2103;
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
                    mView.showResourceView(groupState, mActiveGroup.getName());
                    break;
                default:
                    break;
            }
        }
    };
    private HueMe mApp;
    private BridgeResourceDetailActivityView mView;
    private PHGroup mActiveGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridgeresource_detail);

        this.mApp = (HueMe) getApplication();

        mView = new BridgeResourceDetailActivityView(this);

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
        // Update mActiveGruop
        Map<String, PHGroup> groups = mApp.getHueService().getBridge().getResourceCache().getGroups();
        mActiveGroup = groups.get(mActiveGroup.getIdentifier());

        // Show new state
        PHLightState updatedGroupState = this.getGroupState();
        this.mView.showResourceView(updatedGroupState, mActiveGroup.getName());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            this.setResult(RESULT_OK);
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public PHLightState getBridgeResourceState() {
        return this.getGroupState();
    }

    @Override
    public void updateBridgeResourceState(PHLightState state)
    {
        // Obtain service
        HueService service = mApp.getHueService();
        // Perform magic
        PHBridge bridge = service.getBridge();
        bridge.setLightStateForGroup(mActiveGroup.getIdentifier(), state, this);
    }

    @Override
    public void changeBridgeResourceName(String newName)
    {
        mActiveGroup.setName(newName);

        // Obtain service
        HueService service = mApp.getHueService();
        // Perform magic
        PHBridge bridge = service.getBridge();
        bridge.updateGroup(mActiveGroup, this);
    }

    @Override
    public void deleteBridgeResourcePermanently()
    {
        // Obtain service
        HueService service = mApp.getHueService();
        // Perform magic
        PHBridge bridge = service.getBridge();
        bridge.deleteGroup(mActiveGroup.getIdentifier(), this);

        this.setResult(RESULTCODE_GROUP_DELETED);
        this.finish();
    }
}