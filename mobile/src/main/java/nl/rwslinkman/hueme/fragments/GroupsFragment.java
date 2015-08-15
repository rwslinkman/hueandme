package nl.rwslinkman.hueme.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHGroup;

import java.util.ArrayList;
import java.util.List;

import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.ui.HueGroupsAdapter;
import nl.rwslinkman.hueme.ui.HueIPAddressAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment
{
    private static final String TAG = GroupsFragment.class.getSimpleName();
    private PHBridge mActiveBridge;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        List<PHGroup> hueGroups = this.mActiveBridge.getResourceCache().getAllGroups();
        HueGroupsAdapter adapter = new HueGroupsAdapter(hueGroups);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.groups_list_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);

        return rootView;
    }

    public static GroupsFragment newInstance()
    {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setActiveBridge(PHBridge activeBridge)
    {
        this.mActiveBridge = activeBridge;
    }
}
