package com.huiges.AndroBlip;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CommentService extends IntentService {


	public CommentService() {
		super("CommentService");
		if(C.VERBOSE){Log.d(C.TAG,"commentservice started");}
	}

	/**
	 * The IntentService calls this method from the default worker thread with
	 * the intent that started the service. When this method returns, IntentService
	 * stops the service, as appropriate.
	 * 
	 * http://developer.android.com/guide/topics/ui/notifiers/notifications.html
	 */
	@Override
	protected void onHandleIntent(Intent intent) { 
		if(C.VERBOSE){Log.d(C.TAG,"commentservcie: onhandleintent");}

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		BlipNotifications bn = new BlipNotifications(getApplicationContext());
		bn.notify(mNotificationManager);
				
	}	
		

}
