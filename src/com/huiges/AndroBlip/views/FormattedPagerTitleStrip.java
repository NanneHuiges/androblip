package com.huiges.AndroBlip.views;

import com.huiges.AndroBlip.C;

import android.content.Context;
import android.support.v4.view.PagerTitleStrip;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Simple extention for PagerTitleStrip that changes the typeface
 * 
 * @author Nanne Huiges
 *
 */
public class FormattedPagerTitleStrip extends PagerTitleStrip {
    public FormattedPagerTitleStrip(Context context) {
        super(context);
    }
    public FormattedPagerTitleStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
	    
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        for (int i=0; i<this.getChildCount(); i++) {
            if (this.getChildAt(i) instanceof TextView) {
                ((TextView)this.getChildAt(i)).setTypeface(C.Georgia(getContext()));
            }
        }
    }
}

