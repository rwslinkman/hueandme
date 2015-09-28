package nl.rwslinkman.hueme.fragments;

import android.view.MenuItem;
import android.view.View;

/**
 * @author Rick Slinkman
 */
public abstract class AbstractActionMenuFragment extends AbstractFragment
{
    public abstract int getMenuResource();
    public abstract boolean handleMenuItemClick(MenuItem item);
    public abstract int getLayoutResource();
    public abstract void createFragment(View rootView);
}
