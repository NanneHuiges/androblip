package com.huiges.AndroBlip;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;

public class OrderPreference extends Preference {

	public OrderPreference(Context context) {
		super(context);
	}
	
	public OrderPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public OrderPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	@Override
	protected void onClick(){
		Intent myIntent1 = new Intent(getContext(), BlipViewOrderPreference.class);
		getContext().startActivity(myIntent1);
	}

}
