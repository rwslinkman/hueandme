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

import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.rwslinkman.hueme.MainActivity;
import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.service.HueService;
import nl.rwslinkman.hueme.ui.BridgeResourceSwitchAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends AbstractActionMenuFragment implements BridgeResourceSwitchAdapter.OnBridgeResourceItemEventListener<PHGroup>
{
    public static final String TAG = GroupsFragment.class.getSimpleName();
    private RecyclerView mGroupsListView;
    private BroadcastReceiver groupsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            switch(intent.getAction())
            {
                case HueService.HUE_GROUPS_STATE_UPDATE:
                    Log.d(TAG, "Group state updated");
                    break;
                case HueService.HUE_LIGHTS_STATE_UPDATE:
                    Log.d(TAG, "Lights state updated");
                    break;
                case HueService.HUE_POSSIBLE_STATE_UPDATE:
                    Log.d(TAG, "Possible state update");
                    break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();

        if(this.mActiveBridge != null)
        {
            // Prepare data
            List<PHGroup> hueGroups = this.mActiveBridge.getResourceCache().getAllGroups();
            String noGroupsText = this.getString(R.string.groups_nogroups_text);

            Map<PHGroup, Boolean> hueStateGroups = new HashMap<>();
            for(PHGroup group : hueGroups)
            {
                PHLightState groupState = this.getGroupState(group);
                hueStateGroups.put(group, groupState.isOn());
            }

            // Prepare adapter
            BridgeResourceSwitchAdapter<PHGroup> groupsAdapter = new BridgeResourceSwitchAdapter<>(this.getResources(), hueStateGroups, noGroupsText);
            groupsAdapter.setOnBridgeResourceItemEventListener(this);

            // Insert adapter
            mGroupsListView.setHasFixedSize(true);
            mGroupsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mGroupsListView.setAdapter(groupsAdapter);
        }
        else
        {
            Log.e(TAG, "No bridge set :(");
        }

        this.registerUpdates(this.groupsReceiver, this.getGroupUpdatesIntentFilter());
    }

    public PHLightState getGroupState(PHGroup group)
    {
        Map<String, PHLight> lights = this.mActiveBridge.getResourceCache().getLights();
        List<String> lightsInGroup = group.getLightIdentifiers();
        if (!lightsInGroup.isEmpty())
        {
            String repLightID = lightsInGroup.get(0);
            return lights.get(repLightID).getLastKnownLightState();
        }
        return null;
    }

    @Override
    public int getMenuResource()
    {
        return R.menu.toolbarmenu_groups;
    }

    @Override
    public boolean handleMenuItemClick(MenuItem item)
    {
        if(item.getItemId() == R.id.groups_add_action)
        {
            ((MainActivity)getActivity()).getView().displayAddGroup();
            return true;
        }
        return false;
    }

    @Override
    public int getLayoutResource()
    {
        return R.layout.fragment_groups;
    }

    @Override
    public void createFragment(View rootView)
    {
        this.mGroupsListView = (RecyclerView) rootView.findViewById(R.id.groups_list_view);
    }

    public static GroupsFragment newInstance()
    {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_FRAGMENT_MARKER, FragmentMarker.Groups.getValue());
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    @Override
    public void onBridgeResourceItemClicked(PHGroup clickedItem)
    {
        ((MainActivity) getActivity()).startDetailActivity(clickedItem);
    }

    @Override
    public void onBridgeResourceItemSwitchChanged(PHGroup clickedItem, boolean isChecked)
    {
        if(this.mActiveBridge != null)
        {
            PHLightState state = new PHLightState();
            state.setOn(isChecked);

            this.mActiveBridge.setLightStateForGroup(clickedItem.getIdentifier(), state);
        }
        else
        {
            Log.e(TAG, "No bridge set :(");
        }
    }

    public IntentFilter getGroupUpdatesIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HueService.HUE_GROUPS_STATE_UPDATE);
        intentFilter.addAction(HueService.HUE_LIGHTS_STATE_UPDATE);
        intentFilter.addAction(HueService.HUE_POSSIBLE_STATE_UPDATE);
        return intentFilter;
    }
}
