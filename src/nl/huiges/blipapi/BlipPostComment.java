package nl.huiges.blipapi;

import nl.huiges.apicaller.APICaller;
import nl.huiges.apicaller.SimpleCaller;
import nl.huiges.apicaller.iAPIResultReceiver;
import android.content.Context;
import android.os.Bundle;

public class BlipPostComment extends BlipAPI implements iAPIResultReceiver {
	private Context context;
	private iAPIResultReceiver receiver;
	private int signal;
	private Bundle parameter_extras;
	
	public BlipPostComment(Context context){
		this.context = context;
	}
	public  void PostComment(Bundle extras, iAPIResultReceiver receiver, int signal ){
    	
    	this.receiver= receiver; 
    	this.signal = signal;
    	this.parameter_extras = extras;
    	
		BlipNonce bn = new BlipNonce();
		bn.getUserSignature(context, this, 1);
	}
	
	@Override
	public void signal(int signalId, Bundle extras) {
		SimpleCaller caller = new SimpleCaller(receiver, signal,  APICaller.METHOD_POST, APICaller.SCHEME_HTTP,
    			server, "v3/comment.json");
		//		"192.168.178.2", "~nanne/testupload");
		
		caller.addParameter("timestamp", extras.getString("STAMP"));
		caller.addParameter("nonce",extras.getString("NONCE"));
		caller.addParameter("token",extras.getString("TOKEN"));
		caller.addParameter("secret",extras.getString("SECRET"));
		caller.addParameter("signature",extras.getString("SIGNA"));
		
	
		
		caller.addParameter("entry_id", parameter_extras.getString("entry_id"));
		caller.addParameter("reply_to_comment_id", parameter_extras.getString("reply_to_comment_id"));
		caller.addParameter("comment", parameter_extras.getString("comment"));

		//gmt_offset
		
		caller.execute();	
		
	}
	
}
