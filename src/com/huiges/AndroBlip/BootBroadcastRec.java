package com.huiges.AndroBlip;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootBroadcastRec extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		if(C.VERBOSE){Log.d(C.TAG,"bootbroadcastreceiver onreceive");}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);		
		if(!prefs.getBoolean("use_notify", true)){
			Intent i=new Intent(context, CommentService.class);
			PendingIntent pi=PendingIntent.getService(context, 0, i, 0);
			pi.cancel();
		}else{
			AlarmManager mgr=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			Intent i=new Intent(context, CommentService.class);
			PendingIntent pi=PendingIntent.getService(context, 0, i, 0);
			long freq = C.getNotifyFrequency(context);
			mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
					SystemClock.elapsedRealtime(), 
					freq, 
					pi);
		}
	}

}