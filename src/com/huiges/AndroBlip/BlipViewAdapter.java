package com.huiges.AndroBlip;

import java.util.ArrayList;

import nl.huiges.blipapi.ViewItemWAO;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class BlipViewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ViewItemWAO> blipViewItems;
    
    public BlipViewAdapter(Context c) {
        mContext = c;
        blipViewItems = new ArrayList<ViewItemWAO>();  
    }
    
    public void Update(ArrayList<ViewItemWAO> blipViewItems){
    	this.blipViewItems = blipViewItems;
    	this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return blipViewItems.size();
    }

    @Override
    public ViewItemWAO getItem(int position) {
        return blipViewItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(getItem(position).getEntry_id());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
        	
        	DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) mContext
                    .getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);

        	int width = metrics.widthPixels;
        	
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(width/3, width/3));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(0, 0, 2, 0);
        } else {
            imageView = (ImageView) convertView;
        }

        //imageView.setImageResource(mThumbIds[position]);

		String imageUrl =  blipViewItems.get(position).getThumbnail();
		
		ImageLoader.getInstance().displayImage(imageUrl, imageView); 
		
        return imageView;
    }


}