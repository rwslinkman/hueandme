package nl.rwslinkman.hueme.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.rwslinkman.hueme.R;

public class ConnectingFragment extends Fragment
{
    private String mConnectingIP;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_connecting, container, false);

//        TextView ipAddressView = (TextView) rootView.findViewById(R.id.connecting_ipaddress_view);
//        if(!mConnectingIP.isEmpty())
//        {
//            ipAddressView.setText(mConnectingIP);
//        }

        return rootView;
    }

    public void setConnectingIP(String ip)
    {
        this.mConnectingIP = ip;
    }


    public static ConnectingFragment newInstance()
    {
        ConnectingFragment fragment = new ConnectingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
}
