package com.huiges.AndroBlip;

import java.util.HashMap;
import java.util.List;

import com.huiges.AndroBlip.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DynamicListViewAdapter extends BaseAdapter{

	private Context mContext;
    final int INVALID_ID = -1;
    List<String> objects;
    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();


	public DynamicListViewAdapter(Context context, List<String> objects) {
		super();
		mContext=context;	
		this.objects = objects;
		for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
	}

	public int getCount() {
		return objects.size();
	}

	// getView method is called for each item of ListView
	public View getView(int position,  View view, ViewGroup parent) {
		// inflate the layout for each item of listView
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.dynlistview, null);

		((TextView)view.findViewById(R.id.dynlist_text)).setText((String)getItem(position));

		return view;
	}

	public String getItem(int position) {
		return objects.get(position);
	}

	public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        String item = getItem(position);
        return mIdMap.get(item);
	}
}
