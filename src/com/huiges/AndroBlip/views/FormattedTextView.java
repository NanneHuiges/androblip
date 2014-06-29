package com.huiges.AndroBlip.views;

import com.huiges.AndroBlip.C;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Convenience class to set the font for textviews.
 * 
 * @author Nanne Huiges
 *
 */
public class FormattedTextView extends TextView {
	public FormattedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setGeorgia();
	}
	public FormattedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setGeorgia();
	}
	public FormattedTextView(Context context) {
		super(context);
		setGeorgia();
	}

	
	public void setGeorgia(){
		setTypeface(C.Georgia(getContext()));
	}	
}
