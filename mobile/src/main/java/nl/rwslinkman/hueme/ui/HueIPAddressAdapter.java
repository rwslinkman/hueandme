package nl.rwslinkman.hueme.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.philips.lighting.hue.sdk.PHAccessPoint;

import java.util.List;

import nl.rwslinkman.awesome.ButtonAwesome;
import nl.rwslinkman.hueme.R;

public class HueIPAddressAdapter extends RecyclerView.Adapter<HueIPAddressAdapter.ViewHolder>
{
    private List<PHAccessPoint> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        // each data item is just a string in this case
        public TextView mIPaddressView;
        public ButtonAwesome mConnectButton;
        public ViewHolder(View holderView)
        {
            super(holderView);
            mIPaddressView = (TextView) holderView.findViewById(R.id.item_ap_ipaddress);
            mConnectButton = (ButtonAwesome) holderView.findViewById(R.id.item_ap_connect);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HueIPAddressAdapter(List<PHAccessPoint> accessPoints)
    {
        mDataset = accessPoints;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HueIPAddressAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hue_ap, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        PHAccessPoint accessPoint = mDataset.get(position);
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mIPaddressView.setText(accessPoint.getIpAddress());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return mDataset.size();
    }
}