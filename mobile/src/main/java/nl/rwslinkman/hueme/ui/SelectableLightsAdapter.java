package nl.rwslinkman.hueme.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.philips.lighting.model.PHLight;

import java.util.List;

import nl.rwslinkman.awesome.TextAwesome;
import nl.rwslinkman.hueme.R;

public class SelectableLightsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final int VIEW_TYPE_EMPTY_LIST_PLACEHOLDER = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;
    private OnConnectButtonListener mConnectListener;
    private List<PHLight> mDataset;

    public SelectableLightsAdapter(List<PHLight> lights)
    {
        mDataset = lights;
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
//            ViewHolder vh = (ViewHolder) holder;
//            if(vh.isPlaceholderView())
//            {
//                vh.mIconView.setText(R.string.fa_exclamation_circle);
//                vh.mIPaddressView.setText(R.string.item_ap_emptylist);
//                vh.mConnectButton.setVisibility(View.INVISIBLE);
//            }
//            else
//            {
//                final PHLight accessPoint = mDataset.get(position);
//                vh.mIPaddressView.setText(accessPoint);
//                vh.mConnectButton.setOnClickListener(new View.OnClickListener()
//                {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        // Pass click to real listener and provide Hue IP address
//                        mConnectListener.onConnectClick(v, accessPoint);
//                    }
//                });
//            }
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

    public interface OnConnectButtonListener
    {
        void onConnectClick(View connectButton, PHLight ipAddress);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextAwesome mIconView;
        public TextView mIPaddressView;
        public Button mConnectButton;
        private boolean mIsPlaceholder;

        public ViewHolder(View holderView, boolean isPlaceholderView)
        {
            super(holderView);
            this.mIsPlaceholder = isPlaceholderView;
            mIconView = (TextAwesome) holderView.findViewById(R.id.item_ap_icon);
            mIPaddressView = (TextView) holderView.findViewById(R.id.item_ap_ipaddress);
            mConnectButton = (Button) holderView.findViewById(R.id.item_ap_connect);
        }

        public boolean isPlaceholderView()
        {
            return this.mIsPlaceholder;
        }
    }
}