package nl.rwslinkman.hueme.ui;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import nl.rwslinkman.hueme.GroupDetailActivity;
import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.helper.HueColorConverter;
import nl.rwslinkman.hueme.helper.PhilipsHSB;

/**
 * @author Rick Slinkman
 */
public class GroupDetailActivityView implements CompoundButton.OnCheckedChangeListener, ColorPicker.OnColorSelectedListener, DimmerBar.OnDimmerValueSelectedListener
{
    public static final String TAG = GroupDetailActivityView.class.getSimpleName();
    private Toolbar mToolbar;
    private GroupDetailActivity mActivity;
    private Switch mOnOffSwitch;
    private Switch mColorloopSwitch;
    private ColorPicker mColorPickerView;
    private DimmerBar mDimmerView;

    public GroupDetailActivityView(GroupDetailActivity activity)
    {
        this.mActivity = activity;

        // Init views
        this.mToolbar = (Toolbar) this.mActivity.findViewById(R.id.groupdetail_toolbar_view);
        this.mColorPickerView = (ColorPicker) this.mActivity.findViewById(R.id.groupdetail_colorpicker_view);
        this.mDimmerView = (DimmerBar) this.mActivity.findViewById(R.id.groupdetail_dimmer_view);
        this.mOnOffSwitch = (Switch) this.mActivity.findViewById(R.id.groupdetail_onoffswitch_view);
        this.mColorloopSwitch = (Switch) this.mActivity.findViewById(R.id.groupdetail_colorloopswitch_view);
    }

    public void createView(PHGroup group)
    {
        if(group != null)
        {
            mToolbar.setTitle(group.getName());

            PHLightState groupState = this.mActivity.getGroupState();
            showGroupView(groupState);
        }
        // Set Toolbar to be ActionBar
        this.mActivity.setSupportActionBar(mToolbar);
        this.mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.mActivity.getSupportActionBar().setHomeButtonEnabled(true);

        this.mColorPickerView.addSVBar(this.mDimmerView);
    }

    public void registerListeners()
    {
        // Register to UI changes
        this.mOnOffSwitch.setOnCheckedChangeListener(this);
        this.mColorloopSwitch.setOnCheckedChangeListener(this);
        this.mColorPickerView.setOnColorSelectedListener(this);
        this.mDimmerView.setOnDimmerValueSelectedListener(this);
    }


    public void showGroupView(final PHLightState state)
    {
        mActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                int color = HueColorConverter.convertStateToColor(state);

                // On/Off switch
                mOnOffSwitch.setChecked(state.isOn());
                // ColorPicker
                mColorPickerView.setColor(color);
                mColorPickerView.setOldCenterColor(color);
                mColorPickerView.setNewCenterColor(color);

                setAllViewsEnabled(true);
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if(buttonView.getId() == R.id.groupdetail_onoffswitch_view)
        {
            // Create light state
            PHLightState state = new PHLightState();
            state.setOn(isChecked);
            state.setEffectMode(PHLight.PHLightEffectMode.EFFECT_NONE);

            updateGroupState(state);
        }
        else if(buttonView.getId() == R.id.groupdetail_colorloopswitch_view)
        {
            // Create light state
            PHLightState state = new PHLightState();
            PHLight.PHLightEffectMode mode = (isChecked) ? PHLight.PHLightEffectMode.EFFECT_COLORLOOP : PHLight.PHLightEffectMode.EFFECT_NONE;
            state.setEffectMode(mode);

            updateGroupState(state);
        }
    }

    @Override
    public void onColorSelected(int selectedColor)
    {
        PhilipsHSB color = HueColorConverter.convertColorToHSB(selectedColor);
        Log.d(TAG, "onColorSelected: " + color.toString());

        mColorPickerView.setOldCenterColor(selectedColor);
        mColorPickerView.setNewCenterColor(selectedColor);

        // Create light state
        PHLightState state = new PHLightState();
        state.setOn(true);
        state.setHue(color.getHue());
        state.setBrightness(color.getBrightness());
        state.setSaturation(color.getSaturation());

        updateGroupState(state);
    }

    @Override
    public void onDimmerValueSelected(int dimmerValue)
    {
        PhilipsHSB color = HueColorConverter.convertColorToHSB(dimmerValue);

        mColorPickerView.setOldCenterColor(dimmerValue);
        mColorPickerView.setNewCenterColor(dimmerValue);

        // Create light state
        PHLightState state = new PHLightState();
        state.setOn(true);
        state.setHue(color.getHue());
        state.setBrightness(color.getBrightness());
        state.setSaturation(color.getSaturation());

        updateGroupState(state);
    }

    private void setAllViewsEnabled(boolean enabled)
    {
        this.mToolbar.setEnabled(enabled);
        this.mColorPickerView.setEnabled(enabled);
        this.mDimmerView.setEnabled(enabled);
        this.mOnOffSwitch.setEnabled(enabled);
    }

    private void updateGroupState(PHLightState state)
    {
        mActivity.updateGroupState(state);
        setAllViewsEnabled(false);
    }
}
