package nl.rwslinkman.hueme.fragments;


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

import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.ui.HueGroupsAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment implements AdapterView.OnItemClickListener
{
    private static final String TAG = GroupsFragment.class.getSimpleName();
    private PHBridge mActiveBridge;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        PHGroup group = (PHGroup) parent.getItemAtPosition(position);
        Toast.makeText(getActivity(), "Item " + group.getName() + " was clicked", Toast.LENGTH_SHORT).show();
    }
}
