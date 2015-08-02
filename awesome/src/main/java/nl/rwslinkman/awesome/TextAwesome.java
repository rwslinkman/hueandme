package nl.rwslinkman.awesome;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.LruCache;
import android.widget.TextView;

/**
 * clas TextAwesome
 * This class comes with the following:
 * - fontawesome.xml values to be used
 * - assets/fonts/fontawesome-webfont.ttf
 * @author Unknown
 * @author Rick Slinkman
 */
public class TextAwesome extends TextView
{
    // Constants
    private final static String NAME = "FONTAWESOME";
    private static final String FONT = "fonts/fontawesome-webfont.ttf";
    private static LruCache<String, Typeface> sTypefaceCache = new LruCache<String, Typeface>(12);

    /**
     * Constructor
     * @param context Context
     */
    public TextAwesome(Context context) {
        super(context);
        init();

    }

    /**
     * Constructor
     * @param context Context
     * @param attrs AttributeSet
     */
    public TextAwesome(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public void init()
    {
        Typeface typeface = sTypefaceCache.get(NAME);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(getContext().getAssets(), FONT);
            sTypefaceCache.put(NAME, typeface);
        }
        setTypeface(typeface);
    }

}
