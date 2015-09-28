package nl.rwslinkman.hueme.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHGroup;

import java.util.List;

import nl.rwslinkman.hueme.GroupDetailActivity;
import nl.rwslinkman.hueme.MainActivity;
import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.ui.HueGroupsAdapter;
import nl.rwslinkman.hueme.ui.MainActivityView;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends AbstractFragment implements AdapterView.OnItemClickListener
{
    public static final String TAG = GroupsFragment.class.getSimpleName();
    private PHBridge mActiveBridge;

    @Override
    public int getLayoutResource()
    {
        return R.layout.fragment_groups;
    }

    @Override
    public FragmentMarker getFragmentMarker()
    {
        return FragmentMarker.Groups;
    }

    @Override
    public void createFragment(View rootView)
    {
        RelativeLayout emptyView = (RelativeLayout) rootView.findViewById(R.id.groups_emptyview);

        List<PHGroup> hueGroups = this.mActiveBridge.getResourceCache().getAllGroups();
        HueGroupsAdapter adapter = new HueGroupsAdapter(getActivity(), hueGroups);

        ListView listView = (ListView) rootView.findViewById(R.id.groups_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        if(hueGroups.isEmpty())
        {
            listView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    public static GroupsFragment newInstance()
    {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    public void setActiveBridge(PHBridge activeBridge)
    {
        this.mActiveBridge = activeBridge;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        PHGroup group = (PHGroup) parent.getItemAtPosition(position);
        ((MainActivity) getActivity()).startDetailActivity(group);
    }
}
