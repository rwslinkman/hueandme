package nl.rwslinkman.hueme.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.philips.lighting.model.PHGroup;

import java.util.List;

import nl.rwslinkman.awesome.TextAwesome;
import nl.rwslinkman.hueme.R;

public class HueGroupsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<PHGroup> mDataset;

    public HueGroupsAdapter(List<PHGroup> accessPoints)
    {
        mDataset = accessPoints;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hue_groups_item, parent, false);
        return new ViewHolder(v, mDataset.isEmpty());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder instanceof HueGroupsAdapter.ViewHolder)
        {
            ViewHolder vh = (ViewHolder) holder;
            if(vh.isPlaceholderView())
            {
                vh.mIconView.setText(R.string.fa_exclamation_circle);
                vh.mGroupNameView.setText(R.string.groups_item_nonefound);
            }
            else
            {
                final PHGroup group = mDataset.get(position);
                vh.mIconView.setText(R.string.fa_lightbulb_o);

            }
        }
    }

    @Override
    public int getItemCount()
    {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private boolean mIsPlaceholder;
        public TextAwesome mIconView;
        public TextView mGroupNameView;

        public ViewHolder(View holderView, boolean isPlaceholderView)
        {
            super(holderView);
            this.mIsPlaceholder = isPlaceholderView;
            // do some findViewById magic
            this.mIconView = (TextAwesome) holderView.findViewById(R.id.groups_item_icon);
            this.mGroupNameView = (TextView) holderView.findViewById(R.id.groups_item_name);
        }

        public boolean isPlaceholderView()
        {
            return this.mIsPlaceholder;
        }
    }
}
