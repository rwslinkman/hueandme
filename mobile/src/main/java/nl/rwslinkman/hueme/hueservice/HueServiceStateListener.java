package nl.rwslinkman.hueme.hueservice;

public interface HueServiceStateListener
{
    void onHueServiceReady();
    void onHueServiceHalted();
}
