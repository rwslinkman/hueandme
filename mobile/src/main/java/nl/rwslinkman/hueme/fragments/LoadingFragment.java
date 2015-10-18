package nl.rwslinkman.hueme.fragments;

import android.os.Bundle;
import android.view.View;

import nl.rwslinkman.hueme.R;

public class LoadingFragment extends AbstractFragment
{
    @Override
    public int getLayoutResource()
    {
        return R.layout.fragment_loading;
    }

    @Override
    public void createFragment(View rootView)
    {
        // This fragment does not require action with any UI components
    }

    public static LoadingFragment newInstance()
    {
        LoadingFragment fragment = new LoadingFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_FRAGMENT_MARKER, FragmentMarker.Loading.getValue());
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(false);
        return fragment;
    }
}
