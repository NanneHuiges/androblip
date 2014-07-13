package nl.huiges.blipapi;

import nl.huiges.apicaller.APICaller;
import nl.huiges.apicaller.SimpleCaller;
import nl.huiges.apicaller.iAPIResultReceiver;
import android.content.Context;
import android.os.Bundle;

public class BlipPostEntry extends BlipAPI implements iAPIResultReceiver {
	private Context context;
	private iAPIResultReceiver receiver;
	private int signal;
	private Bundle parameter_extras;
	
	public BlipPostEntry(Context context){
		this.context = context;
	}
	
	public void postEntryData(Bundle extras, iAPIResultReceiver receiver, int signal ){    	
    	this.receiver= receiver; 
    	this.signal = signal;
    	this.parameter_extras = extras;
    	
		BlipNonce bn = new BlipNonce();
		bn.getUserSignature(context, this, 1);
	}
	
	
	@Override
	public void signal(int signalId, Bundle extras) {
		SimpleCaller caller = new SimpleCaller(receiver, signal,  APICaller.METHOD_POST, APICaller.SCHEME_HTTP,
    			server, "v3/entry.json");
		
		caller.addParameter("timestamp", extras.getString("STAMP"));
		caller.addParameter("nonce",extras.getString("NONCE"));
		caller.addParameter("token",extras.getString("TOKEN"));
		caller.addParameter("secret",extras.getString("SECRET"));
		caller.addParameter("signature",extras.getString("SIGNA"));
		caller.addParameter("use_previous_image", "1");	
		caller.addParameter("date", parameter_extras.getString("date"));
		caller.addParameter("title", parameter_extras.getString("title"));
		caller.addParameter("description", parameter_extras.getString("description"));
		caller.addParameter("tags", parameter_extras.getString("tags"));

		//gmt_offset?
		
		caller.execute();	
		
	}

	/* (non-Javadoc)
	 * @see nl.huiges.apicaller.iAPIResultReceiver#showError(java.lang.CharSequence)
	 * 
	 * If lokal api call (blipnonce) generates an error, handle it by sending it on.
	 */
	@Override
	public void showError(CharSequence message) {
		receiver.showError(message);	
	}
	
}
