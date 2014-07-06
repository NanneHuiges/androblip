package com.huiges.AndroBlip;

import com.huiges.AndroBlip.R;
import nl.huiges.apicaller.iAPIResultFragmentReceiver;
import nl.huiges.blipapi.BlipEntry;
import nl.huiges.blipapi.BlipView;
import nl.huiges.blipapi.ViewItemWAO;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
public class FragmentBlipView extends Fragment {
	private int position;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    
		Bundle bundle=getArguments(); 
		position = bundle.getInt(ActivityMain.VIEW_ARGUMENT);
		View rootView = inflater.inflate(R.layout.frag_blipview,container, false);
		
		//https://github.com/chrisbanes/Android-PullToRefresh/wiki/Quick-Start-Guide
		final PullToRefreshGridView ptr_gridview = (PullToRefreshGridView) rootView.findViewById(R.id.blipgridview);
		//GridView gridview = ptr_gridview.getRefreshableView();
		ptr_gridview.setAdapter(new BlipViewAdapter(getActivity()));
		BlipView.updateView(getActivity().getApplicationContext(), 
    			ptr_gridview,
    			BlipView.getViewFromPosition(getActivity().getApplicationContext(), position), 
    			99);

		ptr_gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				new DFragmentLoading()
				  .show( getActivity().getSupportFragmentManager() , DFragmentLoading.TAG_DEFAULTTAG);
	        	ViewItemWAO bvi = (ViewItemWAO) parent.getItemAtPosition(position);
	        	(new BlipEntry(getActivity().getApplicationContext())).updateEntry((iAPIResultFragmentReceiver) getActivity(), bvi.getEntry_id(), null);       
	        }
	    });
	    
		ptr_gridview.setOnRefreshListener(new OnRefreshListener<GridView>() {
		    @Override
		    public void onRefresh(PullToRefreshBase<GridView> refreshView) {
		       //((BlipViewAdapter) ptr_gridview.getRefreshableView().getAdapter()).Update(blipViewItems)
		    	BlipView.updateView(getActivity().getApplicationContext(), 
		    			ptr_gridview,
		    			BlipView.getViewFromPosition(getActivity().getApplicationContext(), position), 
		    			99);
		    	//ptr_gridview.onRefreshComplete();
				NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
				BlipNotifications bn = new BlipNotifications(getActivity().getApplicationContext());
				bn.notify_and_menu(getActivity(),mNotificationManager);
		    }
		});
			
		return rootView;

	}

}