package nl.rwslinkman.hueme.helper;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Rick Slinkman
 */
public class PlaceholderViewHolder extends RecyclerView.ViewHolder
{
    private boolean mIsPlaceholder;

    public PlaceholderViewHolder(View holderView, boolean isPlaceholderView)
    {
        super(holderView);
        this.mIsPlaceholder = isPlaceholderView;
    }

    public boolean isPlaceholderView()
    {
        return this.mIsPlaceholder;
    }
}
