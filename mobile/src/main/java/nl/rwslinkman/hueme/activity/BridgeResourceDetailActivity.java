package nl.rwslinkman.hueme.activity;

import android.support.v7.app.AppCompatActivity;

import com.philips.lighting.model.PHLightState;

import nl.rwslinkman.hueme.ui.BridgeResourceDetailActivityView;

/**
 * @author Rick Slinkman
 */
abstract public class BridgeResourceDetailActivity extends AppCompatActivity
{
    public abstract PHLightState getBridgeResourceState();
    public abstract void updateBridgeResourceState(PHLightState state);
    public abstract void changeBridgeResourceName(String newName);
    public abstract void deleteBridgeResourcePermanently();
}