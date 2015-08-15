package nl.rwslinkman.hueme.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
                // Set values to public Views in vh
            }
            else
            {
                final PHGroup group = mDataset.get(position);
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private boolean mIsPlaceholder;

        public ViewHolder(View holderView, boolean isPlaceholderView)
        {
            super(holderView);
            this.mIsPlaceholder = isPlaceholderView;
            // do some findViewById magic
        }

        public boolean isPlaceholderView()
        {
            return this.mIsPlaceholder;
        }
    }
}
