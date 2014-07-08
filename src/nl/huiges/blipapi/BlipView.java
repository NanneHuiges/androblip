package nl.huiges.blipapi;

import java.util.ArrayList;

import nl.huiges.apicaller.APICaller;
import nl.huiges.apicaller.AdapterCaller;
import nl.huiges.apicaller.iAPIResultReceiver;
import android.content.Context;
import android.os.Bundle;

import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.huiges.AndroBlip.C;
import com.huiges.AndroBlip.FragmentPreference;

public class BlipView extends BlipAPI{
	
	private static final int SIGNAL_AUTHVIEW = 0;

	public static void updateView(Context context, PullToRefreshGridView ptr_gridview,String view,
			int max,int size,int color){
		
		class MyReciever implements iAPIResultReceiver{
			private Context context;
			private PullToRefreshGridView ptr_gridview;
			private String view;
			private int max;
			private int size;
			private int color;
			
			public MyReciever(Context context, PullToRefreshGridView ptr_gridview,String view,
					int max,int size,int color){
				this.context = context;
				this.ptr_gridview = ptr_gridview;
				this.view = view;
				this.max = max;
				this.size = size;
				this.color = color;
			}
			@Override
			public void signal(int signalId, Bundle extras) {
				AdapterCaller caller = new AdapterCaller(context, ptr_gridview, APICaller.METHOD_GET, APICaller.SCHEME_HTTP,  server, "v3/view.json");
				caller.addParameter("timestamp", extras.getString("STAMP"));
				caller.addParameter("nonce",extras.getString("NONCE"));
				caller.addParameter("token",extras.getString("TOKEN"));
				caller.addParameter("secret",extras.getString("SECRET"));
				caller.addParameter("signature",extras.getString("SIGNA"));
				_updateView(caller, view, max, size, color);			
			}	
			
			@Override
			public void showError() {
				//FIXME unimplemented		
			}
		}

		
		if ( needsNonce(view)){
			BlipNonce bn = new BlipNonce();
			bn.getUserSignature(context, new MyReciever(context, ptr_gridview, view, max, size, color), SIGNAL_AUTHVIEW);
		} else {
			AdapterCaller caller = new AdapterCaller(context, ptr_gridview, APICaller.METHOD_GET, APICaller.SCHEME_HTTP, server, "v3/view.json");
			_updateView(caller, view, max, size, color);	
		}
	}


	
	private static void _updateView(AdapterCaller caller, String view, int max,
			int size, int color) {
		caller.addParameter("view", view);
		//caller.addParameter("group_id", ?);
		caller.addParameter("max", max);
		caller.addParameter("size", size);
		caller.addParameter("color", color);
		caller.execute();	
	}



	public static boolean needsNonce(String view) {	
		return (view.equals(VIEW_SUBS)  ||
				view.equals(VIEW_ME)    ||
				view.equals(VIEW_MYFAV) ) ;
	}

	public static void updateView(Context context, PullToRefreshGridView ptr_gridview,String view, int max){
		updateView(context, ptr_gridview,view,max,SIZE_BIG,THUMB_COLOR);
	}

	public static void updateView(Context context, PullToRefreshGridView adapter,String view){
		updateView(context, adapter,view,12,SIZE_BIG,THUMB_COLOR);
	}
	
	public static int getCount(Context context){
		if ( C.isLoggedIn(context)){
			return 8;
		} else {
			return 5;
		}
	}
	

	public static CharSequence getTitleFromPosition(Context context, int position) {
		return getViewFromPosition(context, position);
	}
	
	public static String getViewFromPosition(Context context, int position) {
		ArrayList<String> views = FragmentPreference.getBlipViews(context);
		return views.get(position);
	}
}
