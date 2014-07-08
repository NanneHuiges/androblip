package nl.huiges.blipapi;

import org.json.JSONArray;
import org.json.JSONObject;

import com.huiges.AndroBlip.C;

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
			bn.getUserSignature(context, this, SIGNAL_GETC);
		}
	}


	@Override
	public void signal(int signalId, Bundle extras) {
		switch(signalId){
		case SIGNAL_PARSE:
			//SimpleCaller caller = new SimpleCaller(application, SIGNAL_GETC, APICaller.METHOD_GET, APICaller.SCHEME_HTTP, server, "v3/comments.json");
			//parse extras, 
			extras = parseResult(extras);
			application.signal(signal, extras);
			break;
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
			break;
		}
	}

	private Bundle parseResult(Bundle extras){
		if(extras == null){
			if(C.VERBOSE){Log.d(C.TAG,"extras is null. not good.");}
			extras = new Bundle();
		}
		if (extras.getString(APICaller.RESULT) == null ){
			if(C.VERBOSE){Log.d(C.TAG,"extras is null. not good.");}
			extras.putBoolean(ERROR, true);
			extras.putString(ERRORSTRING, "unspecified error. Sorry :(" );
			return extras;			
		}
		
		
		try {
			JSONObject result = new JSONObject(extras.getString(APICaller.RESULT));
			if(result.has("error") && !result.isNull("error")){
				if(C.VERBOSE){Log.d(C.TAG,"1Error from get-comments: "+result.get("error"));}
				extras.putBoolean(ERROR, true);
				extras.putString(ERRORSTRING, result.getString("error") );
			}else{
				JSONArray data = result.getJSONArray("data");
				if(data.length() > 0){
					JSONObject comment1 = data.getJSONObject(0);
					Integer id = comment1.getInt("comment_id");
					extras.putInt(COMMENTID, id);
					
					Integer unread = comment1.getInt("unread");
					if(unread.equals(1)){
						extras.putBoolean(UNREAD, true);
						
						StringBuilder contents = new StringBuilder();
						contents.append(comment1.getString("display_name"));
						contents.append(": ");			
						String cnt = android.text.Html.fromHtml(comment1.getString("content")).toString();
						contents.append(cnt);
						extras.putString(CONTENT, contents.toString());
						//notifyComments(id, contents.toString());
					}else{
						extras.putBoolean(UNREAD, false);
					}
				}else{
					extras.putBoolean(UNREAD, false);
				}
			}		
		} catch (Exception e) {
			if(C.VERBOSE){Log.e(C.TAG,"4Exception in blipnotification signal: "+e.getMessage());}
			extras.putBoolean(ERROR, true);
			extras.putString(ERRORSTRING, "unspecified error. Sorry :(" );
		}
		
		return extras;
		
	}

	@Override
	public void showError() {
		//FIXME unimplemented		
	}
}
