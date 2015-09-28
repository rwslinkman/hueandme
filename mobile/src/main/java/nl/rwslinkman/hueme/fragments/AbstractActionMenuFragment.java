package nl.rwslinkman.hueme.fragments;

import android.view.MenuItem;
import android.view.View;

/**
 * Created by Rick on 28-9-2015.
 */
public abstract class AbstractActionMenuFragment extends AbstractFragment
{
    public abstract int getMenuResource();
    public abstract boolean handleMenuItemClick(MenuItem item);
    public abstract int getLayoutResource();
    public abstract void createFragment(View rootView);
}
