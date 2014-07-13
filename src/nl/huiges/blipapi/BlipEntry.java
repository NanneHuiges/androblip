package nl.huiges.blipapi;

import nl.huiges.apicaller.APICaller;
import nl.huiges.apicaller.FragmentCaller;
import nl.huiges.apicaller.iAPIResultFragmentReceiver;
import nl.huiges.apicaller.iAPIResultReceiver;
import android.content.Context;
import android.os.Bundle;

import com.huiges.AndroBlip.C;
import com.huiges.AndroBlip.DFragmentEntry;


public class BlipEntry extends BlipAPI implements iAPIResultReceiver {
	
	private static final int SIGNAL_AUTH = 0;
	private static final int SIGNAL_NONAUTH = 1;
	private static final String PATH = "v3/entry.json";
	
	private Context context;
	private iAPIResultFragmentReceiver application;
	private String entry_id;
	private String dismissableFragmentTag;


	public BlipEntry(Context context){
		this.context = context;
	}
	
	public void updateEntry(iAPIResultFragmentReceiver application, String entry_id, String dismissableFragmentTag){
			this.application = application;
			this.entry_id = entry_id;
			this.dismissableFragmentTag = dismissableFragmentTag;
			
			if(C.isLoggedIn(context)){
				BlipNonce bn = new BlipNonce();
				bn.getUserSignature(context, this, SIGNAL_AUTH);
			}else{
				signal(SIGNAL_NONAUTH,null);
			}
	}
	
	
	@Override
	public void signal(int signalId, Bundle extras) {
		DFragmentEntry fragment = new DFragmentEntry();
				
		fragment.setDismissTag(dismissableFragmentTag);
		FragmentCaller caller = new FragmentCaller(application, 
				fragment, APICaller.METHOD_GET, APICaller.SCHEME_HTTP, 
				server, PATH);
		
		caller.addParameter("entry_id", entry_id);
		caller.addParameter("return_extended",    "0"); // Pass 1 to return extra information about the entry.
		caller.addParameter("return_actions",     "1"); // Pass 1 to return data about various actions the authenticated user can perform upon the entry and it's journal.
		caller.addParameter("return_comments",    "2"); // Pass 1 to return a flat array of the entry's comments, in chronological order.
													    // Pass 2 to return a nested array of comments and their replies.
		caller.addParameter("return_ids",         "1"); // Pass 1 to return entry_ids of the previous and next entries in the owner's journal.
		caller.addParameter("return_dimensions",  "0"); // Pass 1 to return the dimensions of the entry's standard and large images (where available).
		caller.addParameter("return_exif",        "0"); // Pass 1 to return selected EXIF data found in the entry's image.
		
		if(C.isLoggedIn(context)){
			caller.addParameter("timestamp", extras.getString("STAMP"));
			caller.addParameter("nonce",extras.getString("NONCE"));
			caller.addParameter("token",extras.getString("TOKEN"));
			caller.addParameter("secret",extras.getString("SECRET"));
			caller.addParameter("signature",extras.getString("SIGNA"));
		}
		
		caller.execute();
	}
	
	@Override
	public void showError(CharSequence message) {
		application.showError(message);
	}
	
}
