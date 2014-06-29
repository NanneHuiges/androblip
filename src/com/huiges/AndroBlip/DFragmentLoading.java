package com.huiges.AndroBlip;


import com.huiges.AndroBlip.views.FormattedTextView;
import com.huiges.AndroBlip.R;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class DFragmentLoading extends DialogFragment  {

	protected static final String TAG_DEFAULTTAG = "defaultfragmenttag";
	protected static final String TAG_ENTRYTAGPREFIX = "entrytagprefix_";

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
	    View rootView = inflater.inflate(R.layout.frag_dialog_loading, container, false);
	    ((FormattedTextView) rootView.findViewById(R.id.loading_text)).setText("loading!");
	    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return rootView;
	}
	
}
