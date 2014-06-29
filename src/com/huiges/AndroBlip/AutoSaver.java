package com.huiges.AndroBlip;


import com.huiges.AndroBlip.views.FormattedTextView;
import com.huiges.AndroBlip.R;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;


/**
 * Saves a form (title, description etc) so when crash or
 * something unexpected occures the user can continue.
 * 
 * Uses preferences to save title, description and tags 
 * Also saves if there is an autosave currently.
 * 
 * @author Nanne Huiges
 *
 */
public class AutoSaver {
	private SharedPreferences prefs;
	
	public AutoSaver(SharedPreferences prefs){
		this.prefs = prefs;
	}
	
	public void clearAutoSave() {		
		prefs.edit().putString(FragmentPreference.PREFKEY_TITLEAUTOSAVE, "").commit();
		prefs.edit().putString(FragmentPreference.PREFKEY_DESCRAUTOSAVE, "").commit();
		prefs.edit().putString(FragmentPreference.PREFKEY_TAGSAUTOSAVE, "").commit();
		prefs.edit().putBoolean(FragmentPreference.PREFKEY_HASAUTOSAVE, false).commit();		
	}

	
	public void showAutoSave(final View rootView) {
		if(C.VERBOSE){Log.d(C.TAG,"showAutoSave()");}
		
		
		if(   prefs.getBoolean(FragmentPreference.PREFKEY_USEAUTOSAVE, true)
		   && prefs.getBoolean(FragmentPreference.PREFKEY_HASAUTOSAVE, false))
		{
			
			if(C.VERBOSE){Log.d(C.TAG,"showAutoSave gogo ");}
			
			final String prevTitleAutoSave = prefs.getString(FragmentPreference.PREFKEY_TITLEAUTOSAVE,"");
			final String prevDescrAutoSave = prefs.getString(FragmentPreference.PREFKEY_DESCRAUTOSAVE,"");
			final String prevTagsAutoSave = prefs.getString(FragmentPreference.PREFKEY_TAGSAUTOSAVE,"");

			FormattedTextView autoSaveView = (FormattedTextView) rootView.findViewById(R.id.formUsePrevious);

			autoSaveView.setVisibility(View.VISIBLE);

			if(C.VERBOSE){Log.d(C.TAG,"showAutoSave gogo "+ autoSaveView.getHeight());}
			
			TranslateAnimation anim=new TranslateAnimation(0.0f,0.0f,-100.0f,0.0f);
			//TranslateAnimation anim = new TranslateAnimation(0,-autoSaveView.getWidth(),0,0);

			anim.setDuration(2000);
			//animate.setAnimationListener(animationOutListener);

			autoSaveView.startAnimation(anim);
			
			autoSaveView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					((EditText) rootView.findViewById(R.id.formTitle)).setText(prevTitleAutoSave);
					((EditText) rootView.findViewById(R.id.formDescr)).setText(prevDescrAutoSave);
					((EditText) rootView.findViewById(R.id.formTags)).setText(prevTagsAutoSave);
				}
			});
		}
		
	}
	
	public void doAutoSave(View rootView){
		if(C.VERBOSE){Log.d(C.TAG,"doAutoSave()");}
		
		if(prefs.getBoolean(FragmentPreference.PREFKEY_USEAUTOSAVE, true)){	
			
			if(C.VERBOSE){Log.d(C.TAG,"doAutoSave gogo ");}

			
			EditText title = (EditText) rootView.findViewById(R.id.formTitle);
			prefs.edit().putString(FragmentPreference.PREFKEY_TITLEAUTOSAVE, title.getText().toString()).commit();

			EditText descr = (EditText) rootView.findViewById(R.id.formDescr);
			prefs.edit().putString(FragmentPreference.PREFKEY_DESCRAUTOSAVE, descr.getText().toString()).commit();

			EditText tags = (EditText) rootView.findViewById(R.id.formTags);
			prefs.edit().putString(FragmentPreference.PREFKEY_TAGSAUTOSAVE, tags.getText().toString()).commit();

			if(    ! title.getText().toString().isEmpty()
				|| ! descr.getText().toString().isEmpty()
				|| ! tags.getText().toString().isEmpty()){		
				prefs.edit().putBoolean(FragmentPreference.PREFKEY_HASAUTOSAVE, true).commit();
			}else{
				prefs.edit().putBoolean(FragmentPreference.PREFKEY_HASAUTOSAVE, false).commit();
			}
		}

	}
	
}
