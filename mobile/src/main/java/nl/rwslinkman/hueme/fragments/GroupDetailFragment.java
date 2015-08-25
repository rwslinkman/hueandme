package nl.rwslinkman.hueme.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.lighting.model.PHGroup;

import nl.rwslinkman.hueme.R;

public class GroupDetailFragment extends Fragment
{
    private PHGroup mActiveGroup;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return (ViewGroup) inflater.inflate(R.layout.fragment_groupdetail, container, false);
    }

    public void setActiveGroup(PHGroup group)
    {
        this.mActiveGroup = group;
    }

    public static GroupDetailFragment newInstance()
    {
        GroupDetailFragment fragment = new GroupDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
}
