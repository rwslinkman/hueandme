package nl.rwslinkman.hueme.service;

public interface HueServiceStateListener
{
    void onHueServiceReady();
    void onHueServiceHalted();
}
