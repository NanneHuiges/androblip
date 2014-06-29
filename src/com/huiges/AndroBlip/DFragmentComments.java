package com.huiges.AndroBlip;

import java.util.List;

import com.huiges.AndroBlip.R;
import nl.huiges.apicaller.APICaller;
import nl.huiges.apicaller.iAPIResultFragmentReceiver;
import nl.huiges.apicaller.iAPIResultReceiver;
import nl.huiges.blipapi.BlipComments;
import nl.huiges.blipapi.NewCommentWAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;


public class DFragmentComments extends DialogFragment implements iAPIResultReceiver{
	private static final int SIGNAL = 0;
	private View rootView;
	private NewCommentAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setRetainInstance(true);

		rootView = inflater.inflate(R.layout.frag_dialog_recent_comments, container, false);

		
		
		rootView.findViewById(R.id.loadingComments).setVisibility(View.VISIBLE);
		

		ListView listView = (ListView) rootView.findViewById(R.id.newcommentslist);


		adapter = new NewCommentAdapter(getActivity(), getActivity().getSupportFragmentManager(), (iAPIResultFragmentReceiver) getActivity());
		listView.setAdapter(adapter);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		boolean mark = prefs.getBoolean(FragmentPreference.PREFKEY_MARKREAD, true);
		BlipComments bc = new BlipComments(getActivity().getApplicationContext());
		bc.getComment(this, SIGNAL, BlipComments.MAXBLIPS, mark);		
		getActivity().invalidateOptionsMenu();
		
		int width = getActivity().getResources().getDisplayMetrics().widthPixels;
		int height = getActivity().getResources().getDisplayMetrics().heightPixels;
		//listView.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT));
		listView.setLayoutParams(new LinearLayout.LayoutParams(width, height));
		
		return rootView;
	}


	@Override
	public void signal(int signalId, Bundle extras) {
		if(C.VERBOSE){Log.d(C.TAG,"signal dfragmentcomment");}
		// Get ListView object from xml
		String result = extras.getString(APICaller.RESULT);
		if(C.VERBOSE){Log.d(C.TAG,"signal result "+result);}
		JSONObject resultO;
		try {
			resultO = new JSONObject(result);
			JSONArray data = resultO.getJSONArray("data");
			List<NewCommentWAO> newCommentItems = NewCommentWAO.entryListFromJSONString(data);
			rootView.findViewById(R.id.loadingComments).setVisibility(View.GONE);
			adapter.Update(newCommentItems);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO error
		
	}
}