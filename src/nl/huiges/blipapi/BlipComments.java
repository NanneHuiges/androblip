package nl.huiges.blipapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huiges.AndroBlip.C;
import com.huiges.AndroBlip.R;

import nl.huiges.apicaller.APICaller;
import nl.huiges.apicaller.SimpleCaller;
import nl.huiges.apicaller.iAPIResultReceiver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;


public class BlipComments extends BlipAPI implements iAPIResultReceiver {
	public static final int MAXBLIPS       = 50;
	private static final int SIGNAL_GETC   = 0;
	private static final int SIGNAL_PARSE  = 1;

	private Context context;
	private iAPIResultReceiver application;
	private int signal;
	private int max;
	private Boolean mark_as_read;


	public static final String ERROR		= "err";
	public static final String ERRORSTRING	= "errs";
	public static final String COMMENTID	= "id";
	public static final String CONTENT		= "cont";
	public static final String UNREAD		= "unr";

	public BlipComments(Context context){
		this.context = context;
	}


	public void getComment(iAPIResultReceiver application, int signal, int max, Boolean mark_as_read){
		this.application = application;
		this.signal = signal;
		this.max = max;
		this.mark_as_read = mark_as_read;
		if(C.isLoggedIn(context)){
			BlipNonce bn = new BlipNonce();
			bn.getUserSignature(context, this, SIGNAL_GETC); //fixme error?
		}
	}


	@Override
	public void signal(int signalId, Bundle extras) {
		switch(signalId){
		case SIGNAL_PARSE:
			Bundle myExtras = parseResult(extras);		
			if( myExtras.getBoolean(ERROR)){
				if(C.VERBOSE){Log.d(C.TAG,"blipcomments: error detected");}
				showError(myExtras.getString(ERRORSTRING) );
				return;
			}
			if(C.VERBOSE){Log.d(C.TAG,"blipcomments: no error detected");}
			application.signal(signal, myExtras);
			return;
		case SIGNAL_GETC:	
			SimpleCaller caller = new SimpleCaller(
					this, 
					SIGNAL_PARSE, 
					APICaller.METHOD_GET, 
					APICaller.SCHEME_HTTP, 
					server, 
					"v3/comments.json");

			caller.addParameter("max", max);
			caller.addParameter("mark_as_read", (mark_as_read ? "1" : "0" ) );
			caller.addParameter("timestamp", extras.getString("STAMP"));
			caller.addParameter("nonce",extras.getString("NONCE"));
			caller.addParameter("token",extras.getString("TOKEN"));
			caller.addParameter("secret",extras.getString("SECRET"));
			caller.addParameter("signature",extras.getString("SIGNA"));
			caller.execute();
			return;
		}
	}

	/**
	 * @todo errors to resource string?
	 * @param extras
	 * @return
	 */
	private Bundle parseResult(Bundle extras){
		//fixme: werkt dit nu nog? 
		if(C.VERBOSE){Log.d(C.TAG,"called parseResult");}

		Bundle myBun = new Bundle();

		if(extras == null || extras.getString(APICaller.RESULT) == null ){
			if(C.VERBOSE){Log.d(C.TAG,"parsing commentsresult: extras is null. not good.");}
			myBun.putBoolean(ERROR, true);
			myBun.putString(ERRORSTRING, "Empty result. Could be a network issue?" );
			return myBun;
		}




		JSONObject result = null;
		String error = context.getResources().getString(R.string.error_unknown);
		try {
			result = new JSONObject(extras.getString(APICaller.RESULT));
			error = result.optString("error");
		} catch (JSONException e1) { //extras is not null, no nullpointer scares
			//deliberate: defaults are all we know.
		}
		
		if( ! error.isEmpty() || result == null){
			if(C.VERBOSE){Log.d(C.TAG,"Error from get-comments: " + error);}
			myBun.putBoolean(ERROR, true);
			myBun.putString(ERRORSTRING, error );
			return myBun;
		}
		
		try {
			JSONArray data = result.getJSONArray("data");
			if(data.length() > 0){
				JSONObject comment1 = data.getJSONObject(0);
				Integer id = comment1.getInt("comment_id");
				myBun.putInt(COMMENTID, id);

				Integer unread = comment1.getInt("unread");
				if(unread.equals(1)){
					myBun.putBoolean(UNREAD, true);

					StringBuilder contents = new StringBuilder();
					contents.append(comment1.getString("display_name"));
					contents.append(": ");			
					String cnt = android.text.Html.fromHtml(comment1.getString("content")).toString();
					contents.append(cnt);
					myBun.putString(CONTENT, contents.toString());
					//notifyComments(id, contents.toString());
					return myBun;
				}
				myBun.putBoolean(UNREAD, false);
				return myBun;
			}
			myBun.putBoolean(UNREAD, false);
			return myBun;
		} catch (Exception e) { //pokemon exception
			if(C.VERBOSE){Log.e(C.TAG,"Blipcomments; Exception in blipnotification signal: "+e.getMessage());}
			myBun.putBoolean(ERROR, true);
			myBun.putString(ERRORSTRING, context.getResources().getString(R.string.error_unknown) );
			return myBun;	
		}
	}

	@Override
	public void showError(CharSequence message) {
		application.showError(message);
	}
}
