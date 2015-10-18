package nl.rwslinkman.hueme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;
import com.philips.lighting.hue.listener.PHGroupListener;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.rwslinkman.hueme.MainActivity;
import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.ui.MainActivityView;
import nl.rwslinkman.hueme.ui.SelectableLightsAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddGroupFragment extends AbstractFragment implements View.OnClickListener, PHGroupListener {
    public static final String TAG = AddGroupFragment.class.getSimpleName();
    private RecyclerView mLightsListRecycler;
    private Button saveButton;
    private SelectableLightsAdapter mSelectableLightsAdapter;
    private AppCompatEditText mGroupNameField;

    @Override
    public int getLayoutResource()
    {
        return R.layout.fragment_add_group;
    }

    @Override
    public void createFragment(View rootView)
    {
        this.mLightsListRecycler = (RecyclerView) rootView.findViewById(R.id.addgroup_lights_list);

        this.mGroupNameField = (AppCompatEditText) rootView.findViewById(R.id.addgroup_groupname_edittext);

        this.saveButton = (Button) rootView.findViewById(R.id.addgroup_save_button);
        this.saveButton.setOnClickListener(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(this.mActiveBridge != null)
        {
            List<PHLight> lightList = this.mActiveBridge.getResourceCache().getAllLights();

            this.mSelectableLightsAdapter = new SelectableLightsAdapter(this.getResources(), lightList);
            mLightsListRecycler.setHasFixedSize(true);
            mLightsListRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
            mLightsListRecycler.setAdapter(mSelectableLightsAdapter);

            if(lightList.isEmpty())
            {
                saveButton.setEnabled(false);
            }
        }
        else
        {
            Log.e(TAG, "No bridge set :(");
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
            List<PHLight> selectedLights = mSelectableLightsAdapter.getSelectedLights();
            if(selectedLights.isEmpty())
            {
                // No lights selected
                Toast.makeText(this.getActivity(), this.getString(R.string.addgroup_lights_noneselected), Toast.LENGTH_SHORT).show();
                return;
            }

            String groupName = this.mGroupNameField.getText().toString();
            if(groupName.isEmpty())
            {
               // No group name entered
                Toast.makeText(this.getActivity(), this.getString(R.string.addgroup_name_empty), Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> identifiers = new ArrayList<>();
            for(PHLight light : selectedLights)
            {
                identifiers.add(light.getIdentifier());
            }

            this.mActiveBridge.createGroup(groupName, identifiers, this);
        }
    }

    @Override
    public void onCreated(PHGroup phGroup)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                // Group creation succeeded
                MainActivity activity = (MainActivity) getActivity();
                MainActivityView view = activity.getView();

                // Show success SnackBar
                String success = AddGroupFragment.this.getString(R.string.addgroup_snackbar_success);
                SnackBar snackbar = new SnackBar(activity, success, null, null);
                snackbar.show();

                // Navigate back to Groups
                view.displayGroups();
            }
        });
    }

    @Override
    public void onReceivingGroupDetails(PHGroup phGroup)
    {
        // No interest in this event
    }

    @Override
    public void onReceivingAllGroups(List<PHBridgeResource> list)
    {
        // No interest in this event
    }

    @Override
    public void onSuccess()
    {
        // No interest in this event
    }

    @Override
    public void onError(int i, String s)
    {
        // No interest in this event
    }

    @Override
    public void onStateUpdate(Map<String, String> map, List<PHHueError> list)
    {
        // No interest in this event
    }
}
