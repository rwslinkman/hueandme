package nl.rwslinkman.hueme.ui;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gc.materialdesign.views.CheckBox;
import com.philips.lighting.model.PHLight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.rwslinkman.awesome.TextAwesome;
import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.helper.PlaceholderViewHolder;

public class SelectableLightsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final int VIEW_TYPE_EMPTY_LIST_PLACEHOLDER = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;
    private List<PHLight> mDataset;
    private Resources mResources;
    private Map<PHLight, Boolean> mSelectedMap;

    public SelectableLightsAdapter(Resources res, List<PHLight> lights)
    {
        this.mResources = res;
        this.mDataset = lights;
        this.mSelectedMap = new HashMap<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_light_selectable, parent, false);
        return new ViewHolder(v, mDataset.isEmpty());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder instanceof SelectableLightsAdapter.ViewHolder)
        {
            final ViewHolder vh = (ViewHolder) holder;
            if(vh.isPlaceholderView())
            {
                vh.mCheckboxView.setVisibility(View.GONE);
                vh.mIndicatorBulbView.setTextColor(this.mResources.getColor(R.color.android_red));
                vh.mLightNameView.setText(this.mResources.getString(R.string.adapter_lightsselectable_nolights));
            }
            else
            {
                final PHLight light = mDataset.get(position);
                this.mSelectedMap.put(light, false);
                vh.mCheckboxView.setOncheckListener(new CheckBox.OnCheckListener() {
                    @Override
                    public void onCheck(CheckBox checkBox, boolean isChecked) {
                        int newIndicatorColor = (isChecked) ? R.color.rwslinkman_blue_light : android.R.color.darker_gray;
                        vh.mIndicatorBulbView.setTextColor(mResources.getColor(newIndicatorColor));

                        mSelectedMap.put(light, isChecked);
                    }
                });
                vh.mLightNameView.setText(light.getName());
            }
        }
    }

    @Override
    public int getItemCount()
    {
        if(mDataset.isEmpty())
        {
            // Must return one because placeholder view counts as item
            return 1;
        }
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return (mDataset.isEmpty()) ? VIEW_TYPE_EMPTY_LIST_PLACEHOLDER : VIEW_TYPE_OBJECT_VIEW;
    }

    public List<PHLight> getSelectedLights()
    {
        List<PHLight> selectedLights = new ArrayList<>();
        for (Map.Entry<PHLight, Boolean> entry :this.mSelectedMap.entrySet())
        {
            if(entry.getValue())
            {
                selectedLights.add(entry.getKey());
            }
        }
        return selectedLights;
    }

    public static class ViewHolder extends PlaceholderViewHolder
    {
        public CheckBox mCheckboxView;
        public TextAwesome mIndicatorBulbView;
        public TextView mLightNameView;


        public ViewHolder(View holderView, boolean isPlaceholderView)
        {
            super(holderView, isPlaceholderView);

            // Items
            this.mCheckboxView = (CheckBox) holderView.findViewById(R.id.item_lightselectable_checkbox);
            this.mIndicatorBulbView = (TextAwesome) holderView.findViewById(R.id.item_lightselectable_indicator);
            this.mLightNameView = (TextView) holderView.findViewById(R.id.item_lightselectable_name);
        }
    }
}