package nl.huiges.apicaller;

import java.util.ArrayList;

import nl.huiges.blipapi.ViewItemWAO;

import org.json.JSONException;

import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.huiges.AndroBlip.BlipViewAdapter;

import android.content.Context;

/**
 * APICaller that 'fills' an adapter.
 * This is a mix of Androblip specific code and API-Caller code.
 * Currently not ready to be standalone probably.
 * 
 * @author Nanne Huiges
 *
 */
public class AdapterCaller extends APICaller {
	@SuppressWarnings("unused")
	private Context context;
	private PullToRefreshGridView ptr_gridview;

	public AdapterCaller(Context context, PullToRefreshGridView ptr_gridview, 
						int method, int scheme,  String server, String path) {
		super(method, scheme, server, path);
		this.context = context;
		this.ptr_gridview = ptr_gridview;
	}

	@Override
	protected void onPostExecute(String result) {
		this.setLastResponseString(result);
		
		//TODO
		//extract this to the adapter so adaptercaller isn't blipfoto specific.
		//prolly: make interface that calls update from resultstring
		
		ArrayList<ViewItemWAO> blipViewItems;
		try {
			blipViewItems = ViewItemWAO.listFromJSONString(result);
			((BlipViewAdapter) ptr_gridview.getRefreshableView().getAdapter()).Update(blipViewItems);
			ptr_gridview.onRefreshComplete();
		} catch (JSONException e) {
			//TODO
		} catch (Exception e){
			//TODO
		}
		
		if ( ((BlipViewAdapter) ptr_gridview.getRefreshableView().getAdapter()).getCount() == 0 ) {
			blipViewItems = ViewItemWAO.getEmptyList();
			((BlipViewAdapter) ptr_gridview.getRefreshableView().getAdapter()).Update(blipViewItems);
		}
	}

}
