package com.huiges.AndroBlip;

import com.huiges.AndroBlip.R;
import nl.huiges.apicaller.APICaller;
import nl.huiges.apicaller.iAPIResultReceiver;
import nl.huiges.blipapi.BlipComments;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

/**
 * Class to show android notifications for certain situations
 * 
 * (disambiguation: blipfoto also has notifications.
 * Both blipfoto-comments and blipfoto-notifications 
 * can show an android-notification with below code)
 * 
 * Blipfoto notification support is currently disabled
 * as there is no setting. This class does support it.
 * 
 * @author Nanne Huiges
 *
 */
public class BlipNotifications implements iAPIResultReceiver{
	public static final int NOT_ID 					= 37;
	public static final int COM_ID					= 42;
	private static final Integer SIGNAL_NOTIFY 		= 1;
	private static final Integer SIGNAL_NOTIFYMENU 	= 2;

	private Context context;
	private Boolean useNot;
	private Boolean useCom;


	private NotificationManager mNotificationManager;
	private FragmentActivity activity;

	public BlipNotifications(Context context) {
		this.context = context;
		if(C.VERBOSE){Log.d(C.TAG,"BlipNotifications construct");}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		useNot =  true;
		useCom =  prefs.getBoolean(FragmentPreference.PREFKEY_NOTIFYCOMMENT, true);
	}

	public void notify(	NotificationManager mNotificationManager ){
		if(C.VERBOSE){Log.d(C.TAG,"BlipNotifications: notify");}
		this.mNotificationManager = mNotificationManager;

		BlipComments bc = new BlipComments(context);
		bc.getComment(this, SIGNAL_NOTIFY, 1, false);
	}
	
	public void notify_and_menu(FragmentActivity activity,
			NotificationManager mNotificationManager) {
		if(C.VERBOSE){Log.d(C.TAG, "BlipNotifications notify and menu");}

		// TODO Auto-generated method stub
		//doe getActivity().invalidateOptionsMenu(); in signal
		this.activity = activity;
		this.mNotificationManager = mNotificationManager;
		BlipComments bc = new BlipComments(context);
		bc.getComment(this, SIGNAL_NOTIFYMENU, 1, false);
	}
	
	private void notifyComments(Integer id, String contents){
		if(C.VERBOSE){Log.d(C.TAG,"BlipNotifications: comments");}
		 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		if(useCom && (id != prefs.getInt(FragmentPreference.PREFKEY_LASTCOMMENTID , 0) ) ){
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt(FragmentPreference.PREFKEY_LASTCOMMENTID, id);
			editor.commit();
		
			int icon = R.drawable.ic_launcher;
			setNotification(contents,ActivityMain.class,COM_ID, icon);
		}
	}

	@SuppressWarnings("unused")
	private void notifyNotifications(){
		if(C.VERBOSE){Log.d(C.TAG,"BlipNotifications: notifications");}
		if(useNot){
			String not = "You've got a new notification.";
			int icon = R.drawable.ic_launcher;
			setNotification(not,ActivityMain.class,NOT_ID, icon);
		}else{
			mNotificationManager.cancel(NOT_ID);
		}
	}


	private void setNotification(String contents, Class<?> cls,Integer mId, int icon) {
		if(C.VERBOSE){Log.d(C.TAG,"BlipNotifications: set notifictaion");}
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
		.setSmallIcon(icon)
		.setContentTitle("You've got bliplove!")
		.setContentText(contents)
		.setAutoCancel(true)
		;
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, cls);
		resultIntent.putExtra(ActivityMain.SHOW_COMMENTS, true);
	
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(cls);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(
						0,
						PendingIntent.FLAG_UPDATE_CURRENT
						);

		mBuilder.setContentIntent(resultPendingIntent);
		//NotificationManager mNotificationManager =   (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(mId, mBuilder.build());


	}


	@Override
	public void signal(int signalId, Bundle extras) {
		if(C.VERBOSE){Log.d(C.TAG, "blipnotifications: singal");}
		if(C.VERBOSE){Log.d(C.TAG, "result: "+extras.getString(APICaller.RESULT));}

		if(extras.getBoolean(BlipComments.ERROR)){
			showError(extras.getString(BlipComments.ERRORSTRING));
		}else{	
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor editor = prefs.edit();
			if(extras.getBoolean(BlipComments.UNREAD)){
				editor.putBoolean(FragmentPreference.PREFKEY_HASUNREADCOMMS, true);
				
				String contents = extras.getString(BlipComments.CONTENT);
				int id = extras.getInt(BlipComments.COMMENTID);
				notifyComments(id, contents.toString());
				if(signalId == SIGNAL_NOTIFYMENU){
					if(C.VERBOSE){Log.d(C.TAG, "blipnotifications: call invalidateoptionsmenu");}
					 activity.invalidateOptionsMenu();
				}else{
					if(C.VERBOSE){Log.d(C.TAG, "blipnotifications: singla says no call invalidate: "+signalId+" =? "+SIGNAL_NOTIFYMENU);}
				}
			}else{
				editor.putBoolean(FragmentPreference.PREFKEY_HASUNREADCOMMS, false);
				mNotificationManager.cancel(COM_ID);
			}
			editor.commit();
		}
	}

	@Override
	public void showError(CharSequence message) {
		if(C.VERBOSE){Log.d(C.TAG, "blipnotifications error "+message);}
		// no body. there's really nothing much we can do I believe
	}
}