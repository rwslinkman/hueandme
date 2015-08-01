package nl.rwslinkman.hueme.hueservice;

/**
 * Created by Rick on 1-8-2015.
 */
public interface HueServiceStateListener
{
    void onHueServiceReady();
    void onHueServiceHalted();
}
