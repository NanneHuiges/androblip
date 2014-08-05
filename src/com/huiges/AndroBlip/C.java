package com.huiges.AndroBlip;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

/**
 * Constants class.
 * Not sure if good practice, but works.
 */
public class C {
	private static Typeface georgiaTypeface; 


	/**
	 * Tag for use in Log.? calls.
	 */
	public static String TAG 		 = "AndroBlip";
	
	/**
	 * Loglevel value. Based on this value the
	 * constants VERBOSE, DEBUG or MIN are true/false
	 */
	public static int LOGLVL 		 = 0;
	
	/**
	 * Loglevel constants: true/false based on LOGLVL value.
	 */
	public static boolean VERBOSE 	 = LOGLVL > 10;
	public static boolean DEBUG   	 = LOGLVL > 5;
	public static boolean MIN     	 = LOGLVL > 0;

	/**
	 * Maximum age the timestamp used for connection with blipfoto
	 */
	public static long MAX_STAMP_AGE = 60*60;

	/**
	 * Returns the used typeface
	 * @param ctx context used to call createFromAsset on
	 * @return the george typeface used in the app
	 */
	public static final Typeface Georgia(Context ctx) { 
		if (georgiaTypeface == null) { 
			georgiaTypeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/Georgia.ttf"); 
		} 
		return georgiaTypeface; 
	}

	/**
	 * Checks through the preferences if user is logged in.
	 * @param context
	 * @return true if the user is logged in
	 */
	public static boolean isLoggedIn(Context context){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(FragmentPreference.PREFKEY_LOGGEDIN, false);		
	}

	/**
	 * Gets the frequency from the settings
	 * @param context
	 * @return the frequency in milliseconds
	 */
	public static long getNotifyFrequency(Context context){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		int freq;
		try{
			freq = Integer.parseInt(sharedPref.getString(FragmentPreference.PREFKEY_NOTIFYFREQ, "1"));	
		}catch (NumberFormatException nfe){
			freq = 1;
		}

		switch(freq){
		case 1:
			return AlarmManager.INTERVAL_FIFTEEN_MINUTES;
		case 2:
			return AlarmManager.INTERVAL_HALF_HOUR;
		case 3:
			return AlarmManager.INTERVAL_HOUR;
		case 4:
			return AlarmManager.INTERVAL_HALF_DAY;
		case 5:
			return AlarmManager.INTERVAL_DAY;
		default:
			return AlarmManager.INTERVAL_FIFTEEN_MINUTES;
		}
	}
	
}
