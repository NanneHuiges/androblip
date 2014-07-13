package nl.huiges.blipapi;

import nl.huiges.apicaller.APICaller;
import nl.huiges.apicaller.SimpleCaller;
import nl.huiges.apicaller.iAPIResultReceiver;
import android.content.Context;
import android.os.Bundle;

public class BlipPostFavourite extends BlipAPI implements iAPIResultReceiver{
	private iAPIResultReceiver receiver;
	private int signal;
	private String entryId;
	private Context context;

	public BlipPostFavourite(Context context){
		this.context = context;
	}
	
	public void favourite(iAPIResultReceiver receiver, int signal, String entryId){
    	this.receiver= receiver; 
    	this.signal = signal;
    	this.entryId = entryId;
    	
		BlipNonce bn = new BlipNonce();
		bn.getUserSignature(context, this, 1);
	}
	
	public void signal(int signalId, Bundle extras) {
		SimpleCaller caller = new SimpleCaller(receiver, signal, APICaller.METHOD_POST, APICaller.SCHEME_HTTP, server, "v3/favourite.json");
		caller.addParameter("entry_id", entryId);
		
		caller.addParameter("timestamp", extras.getString("STAMP"));
		caller.addParameter("nonce",extras.getString("NONCE"));
		caller.addParameter("token",extras.getString("TOKEN"));
		caller.addParameter("secret",extras.getString("SECRET"));
		caller.addParameter("signature",extras.getString("SIGNA"));
		
		
		caller.execute();
	}
	
	@Override
	public void showError(CharSequence message) {
		//FIXME unimplemented		
	}
}
