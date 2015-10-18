package nl.rwslinkman.hueme.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.philips.lighting.model.PHGroup;

import java.util.List;

import nl.rwslinkman.hueme.MainActivity;
import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.ui.BridgeResourceAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends AbstractActionMenuFragment implements BridgeResourceAdapter.OnBridgeResourceItemClickedListener<PHGroup>
{
    public static final String TAG = GroupsFragment.class.getSimpleName();
    private RecyclerView mGroupsListView;
    private BridgeResourceAdapter<PHGroup> mGroupsAdapter;

    @Override
    public void onResume()
    {
        super.onResume();

        if(this.mActiveBridge != null)
        {
            // Prepare data
            List<PHGroup> hueGroups = this.mActiveBridge.getResourceCache().getAllGroups();
            String noGroupsText = this.getString(R.string.groups_nogroups_text);

            // Prepare adapter
            this.mGroupsAdapter = new BridgeResourceAdapter<>(this.getResources(), hueGroups, noGroupsText);
            this.mGroupsAdapter.setOnBridgeResourceItemClickedListener(this);

            // Insert adapter
            mGroupsListView.setHasFixedSize(true);
            mGroupsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mGroupsListView.setAdapter(mGroupsAdapter);
        }
        else
        {
            Log.e(TAG, "No bridge set :(");
        }
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
}
