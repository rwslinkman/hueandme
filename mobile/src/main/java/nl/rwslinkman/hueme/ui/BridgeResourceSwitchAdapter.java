package nl.rwslinkman.hueme.ui;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gc.materialdesign.views.Switch;
import com.philips.lighting.model.PHBridgeResource;

import java.util.List;

import nl.rwslinkman.awesome.TextAwesome;
import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.helper.PlaceholderViewHolder;

public class BridgeResourceSwitchAdapter<T extends PHBridgeResource> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, Switch.OnCheckListener
{
    public static final String TAG = BridgeResourceSwitchAdapter.class.getSimpleName();

    public interface OnBridgeResourceItemEventListener<T extends PHBridgeResource>
    {
        void onBridgeResourceItemClicked(T clickedItem);
        void onBridgeResourceItemSwitchChanged(T clickedItem, boolean isChecked);
    }

    private static final int VIEW_TYPE_EMPTY_LIST_PLACEHOLDER = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;
    private final String mNoResourceAvailableText;
    private List<T> mDataset;
    private Resources mResources;
    private OnBridgeResourceItemEventListener mListener;

    public BridgeResourceSwitchAdapter(Resources res, List<T> items, String noResourceText)
    {
        this.mResources = res;
        this.mDataset = items;
        this.mNoResourceAvailableText = noResourceText;
    }

    public void setOnBridgeResourceItemEventListener(OnBridgeResourceItemEventListener<T> listener)
    {
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bridgeresource_switch, parent, false);
        v.setOnClickListener(this);
        return new ViewHolder(v, mDataset.isEmpty());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder instanceof BridgeResourceSwitchAdapter.ViewHolder)
        {
            final ViewHolder vh = (ViewHolder) holder;

            if(vh.isPlaceholderView())
            {
                // This is a placeholder view
                vh.mIndicatorBulbView.setTextColor(this.mResources.getColor(R.color.android_red));
                vh.mLightNameView.setText(this.mNoResourceAvailableText);
                vh.mOnOffSwitch.setVisibility(View.GONE);
//                this.mResources.getString(R.string.adapter_lightsselectable_nolights)
            }
            else
            {
                // This is a regular view
                T resource = mDataset.get(position);
                vh.mContainer.setTag(resource.getIdentifier());
                vh.mLightNameView.setText(resource.getName());
                vh.mOnOffSwitch.setOncheckListener(this);
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

    public static class ViewHolder extends PlaceholderViewHolder
    {
        // Views
        public RelativeLayout mContainer;
        public TextAwesome mIndicatorBulbView;
        public TextView mLightNameView;
        public Switch mOnOffSwitch;

        public ViewHolder(View holderView, boolean isPlaceholderView)
        {
            super(holderView, isPlaceholderView);
            this.mContainer = (RelativeLayout) holderView;
            this.mIndicatorBulbView = (TextAwesome) holderView.findViewById(R.id.item_bridgeresource_icon);
            this.mLightNameView = (TextView) holderView.findViewById(R.id.item_bridgeresource_name);
            this.mOnOffSwitch = (Switch) holderView.findViewById(R.id.item_bridgeresource_onoff);
        }
    }

    @Override
    public final void onClick(View v)
    {
        if(mListener != null)
        {
            // Identify clicked item
            String identifier = (String) v.getTag();
            T clickedItem = this.getBridgeResourceByIdentifier(identifier);
            // Raise event
            mListener.onBridgeResourceItemClicked(clickedItem);
        }
    }

    @Override
    public void onCheck(Switch view, boolean isChecked)
    {
        if(mListener != null)
        {
            // Identify clicked item
            RelativeLayout parent = (RelativeLayout) view.getParent();
            String identifier = (String) parent.getTag();
            T clickedItem = this.getBridgeResourceByIdentifier(identifier);
            // Raise event
            mListener.onBridgeResourceItemSwitchChanged(clickedItem, isChecked);
        }
    }

    private T getBridgeResourceByIdentifier(String identifier)
    {
        T clickedItem = null;
        for(T item : this.mDataset)
        {
            if(identifier.equals(item.getIdentifier()))
            {
                clickedItem = item;
                break;
            }
        }
        assert clickedItem != null;
        return clickedItem;
    }
}