package nl.huiges.blipapi;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

import nl.huiges.apicaller.APICaller;
import nl.huiges.apicaller.SimpleCaller;
import nl.huiges.apicaller.iAPIResultReceiver;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.huiges.AndroBlip.C;
import com.huiges.AndroBlip.FragmentPreference;


public class BlipNonce extends BlipAPI implements iAPIResultReceiver{	
	private iAPIResultReceiver nonceReceiver;
	private int nonceSignal;
	private SharedPreferences sharedPref;
	
	private final int SIGNALID_TIME_APP = 1;
	private final int SIGNALID_TIME_USR = 2;
		
    public void getUserSignature(Context context, iAPIResultReceiver nonceReceiver, int nonceSignal){
    	this.nonceReceiver = nonceReceiver;
    	this.nonceSignal = nonceSignal;
    	
    	sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

    	long timeStampStamp = sharedPref.getLong(FragmentPreference.PREFKEY_TIMESTAMPSTAMP, 0);  		
    	long localStamp = (new Date()).getTime() / 1000;
    		
    	boolean reloadTimediff = ( timeStampStamp > localStamp   ||  
    					           localStamp - timeStampStamp > C.MAX_STAMP_AGE);
  
    	if( reloadTimediff ){
    		SimpleCaller caller = new SimpleCaller(this, SIGNALID_TIME_USR, APICaller.METHOD_GET, APICaller.SCHEME_HTTP, server, "v3/time.json");
    		caller.execute();	
    	} else {
    		String token  = sharedPref.getString(FragmentPreference.PREFKEY_USER_TOKEN, ""); 
    		String secret = sharedPref.getString(FragmentPreference.PREFKEY_USER_SECRET, "");
    		calcSignature(token,secret);
    	}
    }

	
	
    public void getApplicationSignature(Context context, iAPIResultReceiver nonceReceiver, int nonceSignal){
    	this.nonceReceiver = nonceReceiver;
    	this.nonceSignal = nonceSignal;
    	sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    	
    	
    	long timeStampStamp = sharedPref.getLong(FragmentPreference.PREFKEY_TIMESTAMPSTAMP, 0);  		
    	long localStamp = (new Date()).getTime();
    		
    	boolean reloadTimediff = ( timeStampStamp > localStamp   ||  
    					           localStamp - timeStampStamp > C.MAX_STAMP_AGE);
  
    	if( reloadTimediff ){
    		//calls calcNonce after timestamp is received.
    		SimpleCaller caller = new SimpleCaller(this, SIGNALID_TIME_APP, APICaller.METHOD_GET, APICaller.SCHEME_HTTP, server, "v3/time.json");
    		caller.execute();	
    	} else {
    		calcSignature("",Settings.API_SECRET);
    	}
    }

    private void calcSignature(String token, String secret){
    	//$signature = MD5($timestamp . $nonce . $token . $secret);
      	
    	String nonce;
    	try {
    		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
    		nonce = new BigInteger(160, sr).toString(32);
    	} catch (NoSuchAlgorithmException e) {
    		nonce = "";
    	}
    	
    	long timeStamp = ( (new Date()).getTime() / 1000)
    					+ sharedPref.getLong(FragmentPreference.PREFKEY_TIMESTAMPDIFF, 0);
		
    	
    	
	    String signature = BlipNonce.digest(timeStamp+nonce+token+secret);
	    Bundle bundle = new Bundle();
	    bundle.putString("SIGNA", signature);
	    bundle.putString("TOKEN", token);
	    bundle.putString("SECRET", secret);
	    bundle.putString("STAMP", String.valueOf(timeStamp));
	    bundle.putString("NONCE", nonce);
	    nonceReceiver.signal(nonceSignal, bundle);
    }
    
	@Override
	public void signal(int signalId, Bundle extras) {
		String result = extras.getString(APICaller.RESULT);
		Long theirTime;
		try {
			String stamp = new JSONObject(result).getJSONObject("data").getString("timestamp");
			theirTime = Long.parseLong(stamp);
		} catch (Exception e) {
			//not sure what happened. lets just try and consider it sync?
			theirTime = (new Date()).getTime();
		}
		
		long ourTime = (new Date()).getTime() / 1000 ;
		long diff = theirTime - ourTime;		
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putLong(FragmentPreference.PREFKEY_TIMESTAMPSTAMP, (new Date()).getTime());
		editor.putLong(FragmentPreference.PREFKEY_TIMESTAMPDIFF, diff);
		editor.commit();
		
		switch ( signalId ){
		case SIGNALID_TIME_APP:
			calcSignature("",Settings.API_SECRET);
			break;
		case SIGNALID_TIME_USR:
    		String token  = sharedPref.getString(FragmentPreference.PREFKEY_USER_TOKEN, ""); 
    		String secret = sharedPref.getString(FragmentPreference.PREFKEY_USER_SECRET, "");
    		calcSignature(token,secret);
			break;			
		}
		
		//nonceReceiver.signal(signalId);
	}
	
	@Override
	public void showError() {
		//FIXME unimplemented		
	}
	
	/**
	 * From 'Jasim' code (GPL)
	 * http://bit.ly/9pmfzp
	 * Lazy, but effective
	 */
	private static String digest(String data)		{
		StringBuffer sb = new StringBuffer();
		try
		{
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(data.getBytes());
			byte[] digestBytes = messageDigest.digest();
			
			/* convert to hexstring */
			String hex = null;
			
			for (int i = 0; i < digestBytes.length; i++) 
			{
				hex = Integer.toHexString(0xFF & digestBytes[i]);
				
				if (hex.length() < 2)
				{
					sb.append("0");
				}
				sb.append(hex);
			}
		}
		catch (Exception ex)
		{
			return "";
		}
		
		return sb.toString();
	}
}
