package com.huiges.AndroBlip;

import com.huiges.AndroBlip.views.FormattedTextView;
import com.huiges.AndroBlip.R;
import nl.huiges.apicaller.APICaller;
import nl.huiges.apicaller.iAPIResultReceiver;
import nl.huiges.blipapi.BlipNonce;
import nl.huiges.blipapi.BlipToken;

import org.json.JSONException;
import org.json.JSONObject;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;


public class DFragmentAccount extends DialogFragment implements iAPIResultReceiver{
	private final int SIGNAL_GETSIGNATURE = 1;
	private final int SIGNAL_GETTOKEN	  = 2;
	private View rootView;
	private String tempToken;
	private SharedPreferences sharedPref;
	private ViewGroup container;
	private LayoutInflater inflater;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
	    this.container = container;
	    this.inflater = inflater;
	    
		if ( C.isLoggedIn(getActivity()) ) {
			this.handleLogout();
		}else{
			this.handleLogin();		
		}

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return rootView;
	}


	private void handleLogin(){
		rootView = inflater.inflate(R.layout.frag_dialog_account_login, container, false);
		((FormattedTextView) rootView.findViewById(R.id.login_title)).setText("Hello!");		
		((FormattedTextView) rootView.findViewById(R.id.login_introtxt)).setText("For some features you need to log in.");
		FormattedTextView v;
		v = ((FormattedTextView) rootView.findViewById(R.id.login_explainbullets));
		v.setText(Html.fromHtml(getString(R.string.login_bulletexplain)));
		v.setMovementMethod(LinkMovementMethod.getInstance());

		v = ((FormattedTextView) rootView.findViewById(R.id.login_moreinfo));
		v.setText(Html.fromHtml(getString(R.string.login_moreinfo)));			
		v.setMovementMethod(LinkMovementMethod.getInstance());

		
		Button button = (Button) rootView.findViewById(R.id.token_button);
		final DFragmentAccount that = this;
    	button.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			EditText tmp =  (EditText) rootView.findViewById(R.id.prefsTempToken);
    			tempToken = tmp.getText().toString();
    			BlipNonce bn = new BlipNonce();
    			bn.getApplicationSignature(getActivity(), that, SIGNAL_GETSIGNATURE);	
    		}
    	});	
	}
	
	private void handleLogout() {
		rootView = inflater.inflate(R.layout.frag_dialog_account,       container, false);	
		
		String uname = sharedPref.getString(FragmentPreference.PREFKEY_USER_NAME, "?");
		
		((FormattedTextView) rootView.findViewById(R.id.login_text)).setText("Hi! You're logged in as  "+uname+"!");
		final DFragmentAccount that = this;

		FormattedTextView button = (FormattedTextView) rootView.findViewById(R.id.logout_button);
		button.setText("Logout");
    	button.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			SharedPreferences.Editor editor = sharedPref.edit();
    			
    			editor.remove(FragmentPreference.PREFKEY_USER_NAME);
    			editor.remove(FragmentPreference.PREFKEY_USER_TOKEN);
    			editor.remove(FragmentPreference.PREFKEY_LOGGEDIN);
    			editor.commit();
    		
    			getActivity().getSupportFragmentManager().beginTransaction().remove(that).commit();

    		}
    	});
		
	}


	@Override
	public void signal(int signalId, Bundle extras) {
		
		switch ( signalId ){
		case SIGNAL_GETSIGNATURE:
			//testing with token
			String stamp	 = extras.getString("STAMP");
			String nonce	 = extras.getString("NONCE");
			String token	 = extras.getString("TOKEN");
			String signature = extras.getString("SIGNA");
			BlipToken bt = new BlipToken();
			bt.getToken(getActivity(), this, SIGNAL_GETTOKEN,stamp,nonce,token,signature,tempToken);
			break;
		case SIGNAL_GETTOKEN:
			String result = extras.getString(APICaller.RESULT);
			
			String displayname = null;
			String usertoken = null;
			String usersecret = null;
			try {
				displayname = new JSONObject(result).getJSONObject("data").getString("display_name");
				usertoken   = new JSONObject(result).getJSONObject("data").getString("token");
				usersecret   = new JSONObject(result).getJSONObject("data").getString("secret");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//TODO: error
			
		
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(FragmentPreference.PREFKEY_USER_NAME, displayname);
			editor.putString(FragmentPreference.PREFKEY_USER_TOKEN, usertoken);
			editor.putString(FragmentPreference.PREFKEY_USER_SECRET, usersecret);
			editor.putBoolean(FragmentPreference.PREFKEY_LOGGEDIN, true);
			editor.commit();
			
			//((FormattedTextView) rootView.findViewById(R.id.login_text)).setText("Hallo "+displayname+"!");	
			getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
	
			break;
		}
		
		
		
		
	}
}