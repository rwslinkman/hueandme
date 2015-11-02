package nl.rwslinkman.hueme.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.List;
import java.util.Map;

import nl.rwslinkman.hueme.HueMe;
import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.service.HueService;
import nl.rwslinkman.hueme.ui.BridgeResourceDetailActivityView;

/**
 * @author Rick Slinkman
 */
public class LightDetailActivity extends BridgeResourceDetailActivity implements PHLightListener
{
    public static final String TAG = LightDetailActivity.class.getSimpleName();
    private static final int RESULTCODE_LIGHT_DELETED = 2203;
    public static final String EXTRA_LIGHT_IDENTIFIER = "nl.rwslinkman.hueme.lightdetailactivity.extra.light_identifier";;
    private final BroadcastReceiver lightUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            switch (action)
            {
                case HueService.HUE_LIGHTS_STATE_UPDATE:
                    String groupIdentifier = mActiveLight.getIdentifier();
                    PHBridge bridge = mApp.getHueService().getBridge();
                    mActiveLight = bridge.getResourceCache().getLights().get(groupIdentifier);

                    PHLightState lightState = mActiveLight.getLastKnownLightState();
                    mView.showResourceView(lightState, mActiveLight.getName());
                    break;
                default:
                    break;
            }
        }
    };
    private HueMe mApp;
    private BridgeResourceDetailActivityView mView;
    private PHLight mActiveLight;

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
            String groupIdentifier = intent.getStringExtra(LightDetailActivity.EXTRA_LIGHT_IDENTIFIER);

            PHBridge bridge = mApp.getHueService().getBridge();
            mActiveLight = bridge.getResourceCache().getLights().get(groupIdentifier);
            bridge.updateLight(mActiveLight, this);


        } else
        {
            Log.e(TAG, "Intent is null, no group received");
            Log.e(TAG, "Is there a group? " + Boolean.toString(this.mActiveLight != null));
        }

        mView.createView(mActiveLight);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Register to updates
        HueService service = mApp.getHueService();
        service.registerReceiver(lightUpdateReceiver, this.getGroupUpdatesIntentFilter());

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
        service.unregisterReceiver(lightUpdateReceiver);
    }

    @Override
    public void onSuccess()
    {
        // Update mActiveGruop
        Map<String, PHLight> lights = mApp.getHueService().getBridge().getResourceCache().getLights();
        mActiveLight = lights.get(mActiveLight.getIdentifier());

        // Show new state
        PHLightState updatedGroupState = this.mActiveLight.getLastKnownLightState();
        this.mView.showResourceView(updatedGroupState, mActiveLight.getName());
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
    public void onReceivingLightDetails(PHLight phLight) {

    }

    @Override
    public void onReceivingLights(List<PHBridgeResource> list) {

    }

    @Override
    public void onSearchComplete() {

    }

    @Override
    public PHLightState getBridgeResourceState()
    {
        return mActiveLight.getLastKnownLightState();
    }

    @Override
    public void updateBridgeResourceState(PHLightState state)
    {
        // Obtain service
        HueService service = mApp.getHueService();
        // Perform magic
        PHBridge bridge = service.getBridge();
        bridge.updateLightState(mActiveLight, state, this);
    }

    @Override
    public void changeBridgeResourceName(String newName)
    {
        mActiveLight.setName(newName);

        // Obtain service
        HueService service = mApp.getHueService();
        // Perform magic
        PHBridge bridge = service.getBridge();
        bridge.updateLight(mActiveLight, this);
    }

    @Override
    public void deleteBridgeResourcePermanently()
    {
        // Obtain service
        HueService service = mApp.getHueService();
        // Perform magic
        PHBridge bridge = service.getBridge();
        bridge.deleteLightWithId(mActiveLight.getIdentifier(), this);

        this.setResult(RESULTCODE_LIGHT_DELETED);
        this.finish();
    }
}
