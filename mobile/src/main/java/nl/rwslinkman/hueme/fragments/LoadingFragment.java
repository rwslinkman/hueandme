package nl.rwslinkman.hueme.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import nl.rwslinkman.hueme.R;
import nl.rwslinkman.hueme.ui.MainActivityView;

public class LoadingFragment extends AbstractFragment
{
    @Override
    public int getLayoutResource()
    {
        return R.layout.fragment_loading;
    }

    @Override
    public FragmentMarker getFragmentMarker()
    {
        return FragmentMarker.Loading;
    }

    @Override
    public void createFragment(View rootView)
    {
//        CircularProgressView progressView = (CircularProgressView) rootView.findViewById(R.id.loading_spinner_view);
    }

    public static LoadingFragment newInstance()
    {
        LoadingFragment fragment = new LoadingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
}
