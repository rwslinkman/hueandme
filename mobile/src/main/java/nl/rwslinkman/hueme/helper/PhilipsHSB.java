package nl.rwslinkman.hueme.helper;

/**
 * @author Rick Slinkman
 */
public class PhilipsHSB
{
    private int mHue;
    private int mSaturation;
    private int mBrightness;

    public PhilipsHSB(int hue, int sat, int bri)
    {
        this.mHue = hue;
        this.mSaturation = sat;
        this.mBrightness = bri;
    }

    public int getHue()
    {
        return mHue;
    }

    public int getSaturation()
    {
        return mSaturation;
    }

    public int getBrightness()
    {
        return mBrightness;
    }

    public void setHue(int mHue)
    {
        this.mHue = mHue;
    }

    public void setSaturation(int mSaturation)
    {
        this.mSaturation = mSaturation;
    }

    public void setBrightness(int mBrightness)
    {
        this.mBrightness = mBrightness;
    }

    @Override
    public String toString()
    {
        return "hue: " + Integer.toString(this.mHue) + ", sat: " + Integer.toString(this.mSaturation) + ", bri: " + Integer.toString(this.mBrightness);
    }
}
