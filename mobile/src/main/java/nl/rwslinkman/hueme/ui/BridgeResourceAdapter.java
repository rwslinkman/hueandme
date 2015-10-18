package nl.rwslinkman.hueme.ui;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.philips.lighting.model.PHBridgeResource;

import java.util.List;

import nl.rwslinkman.awesome.TextAwesome;
import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.helper.PlaceholderViewHolder;

public class BridgeResourceAdapter<T extends PHBridgeResource> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener
{
    public static final String TAG = BridgeResourceAdapter.class.getSimpleName();
    public interface OnBridgeResourceItemClickedListener<T extends PHBridgeResource>
    {
        void onBridgeResourceItemClicked(T clickedItem);
    }

    private static final int VIEW_TYPE_EMPTY_LIST_PLACEHOLDER = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;
    private final String mNoResourceAvailableText;
    private List<T> mDataset;
    private Resources mResources;
    private OnBridgeResourceItemClickedListener mListener;

    public BridgeResourceAdapter(Resources res, List<T> items, String noResourceText)
    {
        this.mResources = res;
        this.mDataset = items;
        this.mNoResourceAvailableText = noResourceText;
    }

    public void setOnBridgeResourceItemClickedListener(OnBridgeResourceItemClickedListener<T> listener)
    {
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bridgeresource, parent, false);
        v.setOnClickListener(this);
        return new ViewHolder(v, mDataset.isEmpty());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder instanceof BridgeResourceAdapter.ViewHolder)
        {
            final ViewHolder vh = (ViewHolder) holder;

            if(vh.isPlaceholderView())
            {
                // This is a placeholder view
                vh.mIndicatorBulbView.setTextColor(this.mResources.getColor(R.color.android_red));
                vh.mLightNameView.setText(this.mNoResourceAvailableText);
//                this.mResources.getString(R.string.adapter_lightsselectable_nolights)
            }
            else
            {
                // This is a regular view
                T resource = mDataset.get(position);
                vh.container.setTag(resource.getIdentifier());
                vh.mLightNameView.setText(resource.getName());
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
        public RelativeLayout container;
        public TextAwesome mIndicatorBulbView;
        public TextView mLightNameView;

        public ViewHolder(View holderView, boolean isPlaceholderView)
        {
            super(holderView, isPlaceholderView);
            this.container = (RelativeLayout) holderView;
            this.mIndicatorBulbView = (TextAwesome) holderView.findViewById(R.id.item_bridgeresource_icon);
            this.mLightNameView = (TextView) holderView.findViewById(R.id.item_bridgeresource_name);
        }
    }

    @Override
    public final void onClick(View v)
    {
        if(mListener != null)
        {
            String identifier = (String) v.getTag();
            T clickedItem = this.getBridgeResourceByIdentifier(identifier);
            mListener.onBridgeResourceItemClicked(clickedItem);
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