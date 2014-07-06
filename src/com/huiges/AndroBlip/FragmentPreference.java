package com.huiges.AndroBlip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.huiges.AndroBlip.R;
import nl.huiges.blipapi.BlipAPI;
import nl.huiges.blipapi.BlipView;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class FragmentPreference extends PreferenceFragment  {
	public static final String PREFKEY_LOGGEDIN       = "ablip_isloggedin";
	public static final String PREFKEY_TIMESTAMPSTAMP = "timestamp_retrieved";
	public static final String PREFKEY_TIMESTAMPDIFF  = "timestamp_diff";
	public static final String PREFKEY_USER_NAME      = "user_displayname";
	public static final String PREFKEY_USER_TOKEN     = "user_token";
	public static final String PREFKEY_USER_SECRET    = "user_secret";
	public static final String PREFKEY_USEDISCCACHE   = "pref_usedisccache";
	public static final String PREFKEY_NOTIFYCOMMENT  = "pref_notifycomment";
	public static final String PREFKEY_USEAUTOSAVE    = "pref_useautosave";
	public static final String PREFKEY_TITLEAUTOSAVE  = "upload_titleAutoSave";
	public static final String PREFKEY_DESCRAUTOSAVE  = "upload_descrAutoSave";
	public static final String PREFKEY_TAGSAUTOSAVE   = "upload_tagsAutoSave";
	public static final String PREFKEY_NOTIFYFREQ 	  = "notify_frequency";

	public static final String PREFKEY_MARKREAD 	  = "pref_markasread";
	
	
	public static final String PREFKEY_COMMENTNOTI    = "pref_useommentnoti";
	public static final String PREFKEY_BLIPNOTINOTI   = "pref_useblipnotinoti";
	public static final String PREFKEY_LASTCOMMENTID  = "pref_lastcommentid";
	public static final String PREFKEY_HASAUTOSAVE    = "pref_hasautosave";
	public static final String PREFKEY_BLIPVIEWORDER  = "pref_blipvieworderstring";
	public static final String PREFKEY_HASUNREADCOMMS = "pref_hasunreadcomms";

	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
	}


	/**
	 * View order is either the default from the C (constants) class, or comes from the preferences.
	 * Split the comma separated list. yeah, not pretty
	 * @param context
	 * @return
	 */
	public static ArrayList<String> getBlipViews(Context context){
		StringBuilder sb = new StringBuilder();
		sb.append(BlipAPI.VIEW_ALL).append(",");
		sb.append(BlipAPI.VIEW_SPOT).append(",");
		sb.append(BlipAPI.VIEW_RATED).append(",");
		sb.append(BlipAPI.VIEW_RAND).append(",");
		sb.append(BlipAPI.VIEW_SUBS).append(",");
		sb.append(BlipAPI.VIEW_ME).append(",");
		sb.append(BlipAPI.VIEW_FAV).append(",");
		sb.append(BlipAPI.VIEW_MYFAV);
		
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String viewsString = sharedPref.getString(FragmentPreference.PREFKEY_BLIPVIEWORDER, sb.toString());
		String[] views = viewsString.split(",");
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(views));
		
		if(C.VERBOSE){Log.d(C.TAG,list.toString());}
		
		ArrayList<String> temp = new ArrayList<String>();
		
		if ( ! C.isLoggedIn(context) ){
			if(C.VERBOSE){Log.d(C.TAG,"niet logged in, removing");}
			Iterator<String> it = list.iterator();
			while(it.hasNext()){
			    String str = it.next();
			    if (BlipView.needsNonce(str)){
			    	temp.add(str);
			    	it.remove();;
			    }
			}
		}
		list.addAll(temp);
		if(C.VERBOSE){Log.d(C.TAG,list.toString());}
		return list;
	}
	
	/**
	 * save order as a comma separated list. Not pretty
	 * @param context
	 * @param mBlipViewList
	 */
	public static void setBlipViews(Context context, ArrayList<String> mBlipViewList){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		StringBuilder sb = new StringBuilder();
		for (String s : mBlipViewList){
		    sb.append(s).append(",");
		}
		sharedPref.edit().putString(FragmentPreference.PREFKEY_BLIPVIEWORDER, sb.toString()).commit();
	}



}