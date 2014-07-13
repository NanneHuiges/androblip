package nl.huiges.blipapi;

import nl.huiges.apicaller.APICaller;
import nl.huiges.apicaller.SimpleCaller;
import nl.huiges.apicaller.iAPIResultReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.huiges.AndroBlip.R;

/**
 * Make an API call to send an image to blipfoto.
 * First make a call to receive a nonce, then 
 * post the image and send the result back to the 
 * caller via the iAPIResultReceiver.
 *
 */
public class BlipPostImage extends BlipAPI implements iAPIResultReceiver {
	private Context context;
	private iAPIResultReceiver receiver;
	private int signal;
	private Uri imageUri;
	
	public BlipPostImage(Context context){
		this.context = context;
	}
	
	
	/**
	 * Posts an image to blipfoto. 
	 * First retrieves a nonce with 'this' as receiver.
	 * When result comes back (this.signal), the receivers'
	 * signal is called.
	 * 
	 * @param imageUri
	 * @param receiver the receiver we call .signal on in the end
	 * @param signal signal id for the receiver
	 */
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
			showError(context.getResources().getString(R.string.error_upload_noimage));
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
	
}
