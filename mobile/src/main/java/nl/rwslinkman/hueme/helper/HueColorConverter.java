package nl.rwslinkman.hueme.helper;

import android.graphics.Color;

import com.philips.lighting.model.PHLightState;

/**
 * @author Rick Slinkman
 */
public class HueColorConverter
{
    public static final double hueConversionValue =  182.0416666666667;
    public static final float satConversionValue =  254;
    public static final float briConversionValue =  254;

    public static int convertStateToColor(PHLightState state)
    {
        // Convert state to integer Color
        float[] hsv = new float[3];
        hsv[0] = (float) (state.getHue() / hueConversionValue);
        hsv[1] = state.getSaturation() / satConversionValue;
        hsv[2] = state.getBrightness() / briConversionValue;
        //
        return Color.HSVToColor(hsv);
    }

    public static PhilipsHSB convertColorToHSB(int color)
    {
        // Convert color to HueSaturationBrightness
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        float hue = (float) (hsv[0] * hueConversionValue);
        float sat = hsv[1] * satConversionValue;
        float bri = hsv[2] * briConversionValue;

        return new PhilipsHSB(Math.round(hue), Math.round(sat), Math.round(bri));
    }
}
