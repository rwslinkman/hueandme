package nl.rwslinkman.hueme.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import nl.rwslinkman.hueme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddGroupFragment extends AbstractFragment implements View.OnClickListener
{
    public static final String TAG = AddGroupFragment.class.getSimpleName();

    public AddGroupFragment()
    {
        // Required empty public constructor
    }

    @Override
    public int getLayoutResource()
    {
        return R.layout.fragment_add_group;
    }

    @Override
    public void createFragment(View rootView)
    {
        // TODO: Obtain listview to populate with available lights

        rootView.findViewById(R.id.addgroup_save_button).setOnClickListener(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(this.mActiveBridge != null)
        {
            Log.d(TAG, "Populate list with lights");
        }
        else
        {
            Log.d(TAG, "Wno bridge set :(");
        }
    }

    public static AddGroupFragment newInstance()
    {
        AddGroupFragment fragment = new AddGroupFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_FRAGMENT_MARKER, FragmentMarker.AddGroup.getValue());
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(false);
        return fragment;
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.addgroup_save_button)
        {
            // TODO: Save selected lights into group
            Log.d(TAG, "Add group save button was clicked");
        }
    }
}
