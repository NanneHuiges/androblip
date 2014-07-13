package nl.huiges.blipapi;

import nl.huiges.apicaller.APICaller;
import nl.huiges.apicaller.SimpleCaller;
import nl.huiges.apicaller.iAPIResultReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import com.huiges.AndroBlip.R;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
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
	
	public void PostEntry(Uri imageUri, iAPIResultReceiver receiver, int signal ){
    	this.receiver= receiver; 
    	this.signal = signal;
    	this.imageUri = imageUri;
    	
		BlipNonce bn = new BlipNonce();
		bn.getUserSignature(context, this, 1);
	}
	
	
	private String getRealPathFromURI(Uri uri) throws Exception {
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

	/* (non-Javadoc)
	 * @see nl.huiges.apicaller.iAPIResultReceiver#signal(int, android.os.Bundle)
	 * 
	 * Takes extras from BlipNonce call and calls the actual image post.
	 */
	@Override
	public void signal(int signalId, Bundle extras) {
		SimpleCaller caller = new SimpleCaller(receiver, signal,  APICaller.METHOD_POST, APICaller.SCHEME_HTTP,
    			server, "v3/image.json");
		
		caller.addParameter("timestamp", extras.getString("STAMP"));
		caller.addParameter("nonce",extras.getString("NONCE"));
		caller.addParameter("token",extras.getString("TOKEN"));
		caller.addParameter("secret",extras.getString("SECRET"));
		caller.addParameter("signature",extras.getString("SIGNA"));
		
		try {
			caller.addFilePath(getRealPathFromURI(imageUri));
		} catch (Exception e) {
			showError("Could not find image ;(");
			return;
		}
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
	
	/**
	 * API code (iApiResultReceiver) makes the result 
	 * go straight to the receiver's signal function
	 * 
	 * This is unfortunate as we want to parse it first
	 * and have the parsing be done by this object. 
	 * 
	 * Quick fix was below static function
	 * @todo call receivers signal function with parsed bundle
	 * 
	 * @param extras result bundle as received from API call
	 * @return parsed bundle
	 */
	public static Bundle parseResult(Bundle extras, Resources res){
		String resultS = extras.getString(APICaller.RESULT);
		JSONObject entryO=null;

		Bundle result = new Bundle();
		
		try {
			entryO = new JSONObject(resultS);
		// } catch (JSONException | NullPointerException e1) { java 7 says hi
		} catch (JSONException e1) {
			result.putBoolean("error", true);
			result.putString("resultText",res.getString(R.string.error_upload_unknown));
			return result;
		} catch ( NullPointerException e1) {
			result.putBoolean("error", true);
			result.putString("resultText",res.getString(R.string.error_upload_network));
			return result;
		}
		
		String resultText = "";
		boolean error = false;
		if(entryO != null){
			JSONObject jso = null;
			try {
				jso = (JSONObject) entryO.getJSONObject("error");
			} catch (JSONException e1) {}

			if (jso != null){
				try {
					resultText = jso.getString("message");
				} catch (JSONException e) {
					resultText = res.getString(R.string.error_blipfoto_empty);
				}
				result.putBoolean("error", true);
				result.putString("resultText",resultText);
				return result;
				
			}else{
				//geen error. hopelijk wel data dan.
				try {
					JSONObject data = (JSONObject) entryO.getJSONObject("data");
					resultText  = data.getString("message");
				} catch (JSONException e) {
					error = true;
					resultText = res.getString(R.string.error_blipfoto_unsure);
				}
			}
		}
		
		result.putBoolean("error", error);
		result.putString("resultText",resultText);
		return result;
	}
	
}
