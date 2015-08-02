package nl.rwslinkman.awesome;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.LruCache;
import android.widget.Button;

/**
 * class ButtonAwesome
 * This class comes with the following:
 * - fontawesome.xml values to be used
 * - assets/fonts/fontawesome-webfont.ttf
 * @author Unknown
 * @author Rick Slinkman
 */
public class ButtonAwesome extends Button
{
    private final static String NAME = "FONTAWESOME";
    private static final String FONT = "fonts/fontawesome-webfont.ttf";
    private static LruCache<String, Typeface> sTypefaceCache = new LruCache<String, Typeface>(12);

    public ButtonAwesome(Context context) {
        super(context);
        init();

    }

    public ButtonAwesome(Context context,AttributeSet attrs) {
        super(context,attrs);
        init();
    }

    public ButtonAwesome(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
        init();
    }

    public void init()
    {
        Typeface typeface = sTypefaceCache.get(NAME);
        if (typeface == null)
        {
            typeface = Typeface.createFromAsset(getContext().getAssets(), FONT);
            sTypefaceCache.put(NAME, typeface);
        }
        setTypeface(typeface);
    }
}