package nl.rwslinkman.hueme.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.philips.lighting.model.PHGroup;

import java.util.List;

import nl.rwslinkman.hueme.MainActivity;
import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.ui.HueGroupsAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends AbstractActionMenuFragment implements AdapterView.OnItemClickListener
{
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
        args.putInt(EXTRA_FRAGMENT_MARKER, FragmentMarker.Groups.getValue());
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        PHGroup group = (PHGroup) parent.getItemAtPosition(position);
        ((MainActivity) getActivity()).startDetailActivity(group);
    }
}
