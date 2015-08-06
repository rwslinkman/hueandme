package nl.rwslinkman.hueme.hueservice;

import android.content.Intent;

public class HueBroadcaster
{
    public static final String DISPLAY_NO_BRIDGE_STATE = "display-no-bridge-state";

    private HueService parent;
    private Intent currentIntent;

    public HueBroadcaster(HueService parent)
    {
        this.parent = parent;
        clearIntent();
    }

    public void setAction(String action)
    {
        this.currentIntent.setAction(action);
    }

    public void broadcast()
    {
        parent.sendBroadcast(currentIntent);
        clearIntent();

    }

    public void clearIntent()
    {
        currentIntent = new Intent();
    }

}
