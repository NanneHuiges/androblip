package nl.huiges.blipapi;

import nl.huiges.apicaller.APICaller;
import nl.huiges.apicaller.SimpleCaller;
import nl.huiges.apicaller.iAPIResultReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

public class BlipPostImage extends BlipAPI implements iAPIResultReceiver {
	private Context context;
	private iAPIResultReceiver receiver;
	private int signal;
	private Uri imageUri;
	
	public BlipPostImage(Context context){
		this.context = context;
	}
	public  void PostEntry(Uri imageUri, iAPIResultReceiver receiver, int signal ){
    	
    	this.receiver= receiver; 
    	this.signal = signal;
    	this.imageUri = imageUri;
    	
		BlipNonce bn = new BlipNonce();
		bn.getUserSignature(context, this, 1);
	}
	
	
	public  String getRealPathFromURI(Uri uri) throws Exception {
		String scheme = uri.getScheme();
		if(scheme.equals("file")){
			return uri.getPath();
		}else if (scheme.equals("content")){
			ContentResolver cr = context.getContentResolver();
		    Cursor cursor = cr.query(uri, null, null, null, null); 
		    cursor.moveToFirst(); 
		    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
		    return cursor.getString(idx); 
		}else{
			throw new Exception("Cannot upload from this source :(");
		}
		
	}
	@Override
	public void signal(int signalId, Bundle extras) {
		SimpleCaller caller = new SimpleCaller(receiver, signal,  APICaller.METHOD_POST, APICaller.SCHEME_HTTP,
    			server, "v3/image.json");
		//		"192.168.178.2", "~nanne/testupload");
		
		caller.addParameter("timestamp", extras.getString("STAMP"));
		caller.addParameter("nonce",extras.getString("NONCE"));
		caller.addParameter("token",extras.getString("TOKEN"));
		caller.addParameter("secret",extras.getString("SECRET"));
		caller.addParameter("signature",extras.getString("SIGNA"));
		
		try {
			caller.addFilePath(getRealPathFromURI(imageUri));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return; //TODO
		}
		
		caller.execute();	
		
	}
	
	@Override
	public void showError() {
		//FIXME unimplemented		
	}
	
	//todo
	public static Bundle parseResult(Bundle extras){
		String resultS = extras.getString(APICaller.RESULT);
		JSONObject entryO=null;

		String resultText = "unkown result";
		boolean error = false;

		Bundle result = new Bundle();
		
		try {
			entryO = new JSONObject(resultS);
		} catch (JSONException e1) {
			error = true;
			result.putBoolean("error", error);
			result.putString("resultText",resultText);
			return result;
		}
		
		if(entryO != null){
			JSONObject jso = null;
			try {
				jso = (JSONObject) entryO.getJSONObject("error");
			} catch (JSONException e1) {}

			if (jso != null){
				error = true;
				try {
					resultText = jso.getString("message");
				} catch (JSONException e) {}
				result.putBoolean("error", error);
				result.putString("resultText",resultText);
				return result;
				
			}else{
				//geen error. hopelijk wel data dan.
				try {
					JSONObject data = (JSONObject) entryO.getJSONObject("data");
					resultText  = data.getString("message");
				} catch (JSONException e) {
					error = true;
				}
			}
		}
		
		result.putBoolean("error", error);
		result.putString("resultText",resultText);
		return result;
	}
	
}
