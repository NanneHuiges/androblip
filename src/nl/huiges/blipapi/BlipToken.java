package nl.huiges.blipapi;

import nl.huiges.apicaller.APICaller;
import nl.huiges.apicaller.SimpleCaller;
import nl.huiges.apicaller.iAPIResultReceiver;
import android.content.Context;


public class BlipToken extends BlipAPI{	
	
    public void getToken(Context context, iAPIResultReceiver nonceReceiver, int nonceSignal, 
    					 String timestamp, String nonce, String token, String signature, String temptoken){

    	SimpleCaller caller = new SimpleCaller(nonceReceiver, nonceSignal,  APICaller.METHOD_GET, APICaller.SCHEME_HTTPS, server, "v3/token.json");
    	//timestamp, nonce, token & signature 
    	caller.addParameter("timestamp", timestamp);
    	caller.addParameter("nonce", nonce);
    	caller.addParameter("token", token);
    	caller.addParameter("secret", Settings.API_SECRET);
    	caller.addParameter("signature", signature);
    	caller.addParameter("temp_token", temptoken);
    	caller.execute();	
    }
}
