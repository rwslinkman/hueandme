package nl.rwslinkman.hueme.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.philips.lighting.model.PHGroup;

import java.util.List;

import nl.rwslinkman.awesome.TextAwesome;
import nl.rwslinkman.hueme.R;

public class HueGroupsAdapter extends ArrayAdapter<PHGroup>
{
    private List<PHGroup> mDataset;

    public HueGroupsAdapter(Context context, List<PHGroup> groups)
    {
        super(context, 0, groups);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get the data item for this position
        PHGroup group = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hue_groups_item, parent, false);
        }

        // do some findViewById magic
        TextAwesome mIconView = (TextAwesome) convertView.findViewById(R.id.groups_item_icon);
        TextView mGroupNameView = (TextView) convertView.findViewById(R.id.groups_item_name);

        mIconView.setText(R.string.fa_lightbulb_o);
        mGroupNameView.setText(group.getName());

        return convertView;
    }

}
