package nl.huiges.blipapi;

import nl.huiges.apicaller.APICaller;
import nl.huiges.apicaller.SimpleCaller;
import nl.huiges.apicaller.iAPIResultReceiver;
import android.content.Context;
import android.os.Bundle;

public class BlipPostSubscribe extends BlipAPI implements iAPIResultReceiver{
	private iAPIResultReceiver receiver;
	private int signal;
	private String display_name;
	private Context context;
	private int method;

	public BlipPostSubscribe(Context context){
		this.context = context;
	}
	
	public void subscribe(iAPIResultReceiver receiver, int signal, String display_name){
		this._subscriber(receiver, signal, display_name, APICaller.METHOD_POST);
	}
	
	public void unsubscribe(iAPIResultReceiver receiver, int signal, String display_name){
		this._subscriber(receiver, signal, display_name, APICaller.METHOD_DELETE);
	}
	
	private void _subscriber(iAPIResultReceiver receiver, int signal, String display_name, int method){
    	this.receiver= receiver; 
    	this.signal = signal;
    	this.display_name = display_name;
    	this.method = method;
		BlipNonce bn = new BlipNonce();
		bn.getUserSignature(context, this, 1);
	}
	
	public void signal(int signalId, Bundle extras) {
		SimpleCaller caller = new SimpleCaller(receiver, signal, this.method, APICaller.SCHEME_HTTP, server, "v3/subscription.json");
		caller.addParameter("display_name", display_name);
		
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
