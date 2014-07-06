package com.huiges.AndroBlip;

import java.util.ArrayList;
import java.util.List;

import com.huiges.AndroBlip.views.FormattedTextView;
import com.huiges.AndroBlip.R;
import nl.huiges.apicaller.iAPIResultFragmentReceiver;
import nl.huiges.blipapi.BlipEntry;
import nl.huiges.blipapi.NewCommentWAO;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class NewCommentAdapter extends BaseAdapter {
	private Context mContext;
	private List<NewCommentWAO> newCommentItems;
	private FragmentManager fragmentManager;
	private iAPIResultFragmentReceiver act;

	public NewCommentAdapter(Context c, FragmentManager fragman, iAPIResultFragmentReceiver act) {
		mContext = c;
		fragmentManager = fragman;
		this.act = act;
		newCommentItems = new ArrayList<NewCommentWAO>();  
	}

	public void Update(List<NewCommentWAO> newCommentItems){
		if(C.VERBOSE){Log.d(C.TAG,"update");Log.d(C.TAG, newCommentItems.toString());}
		this.newCommentItems = newCommentItems;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return newCommentItems.size();
	}

	@Override
	public NewCommentWAO getItem(int position) {
		return newCommentItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.valueOf(getItem(position).getComment_id());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(C.VERBOSE){Log.d(C.TAG,"newcomment getview "+position);}
		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(convertView == null){
			if(C.VERBOSE){Log.d(C.TAG,"convertview null");}

			//LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
			
			if(C.VERBOSE){Log.d(C.TAG,"got infalter");}
			convertView = inflater.inflate(R.layout.newcomment_item,null);
			if(C.VERBOSE){Log.d(C.TAG,"convertview inflated");}
		}
		
		//newcommentImage
		final NewCommentWAO comment = getItem(position);
		
		//sorry bout this.
		((FormattedTextView)convertView.findViewById(R.id.new_comment_item_content)).setText(comment.getContentSpanned());		
		((TextView)convertView.findViewById(R.id.new_comment_item_user)).setText(comment.getDisplay_nameSpanned());
		
		ImageView thumbView = ((ImageView)convertView.findViewById(R.id.new_comment_item_thumb));
		String thumbURL = comment.getThumbnail();
		ImageLoader.getInstance().displayImage(thumbURL, thumbView); 
		
		
		LinearLayout iconContainer = (LinearLayout) convertView.findViewById(R.id.new_comment_iconcontainer);
		iconContainer.removeAllViews();
		for (String icon : comment.getIcons()){
			ImageView mImageView = (ImageView) inflater.inflate(R.layout.user_icon,iconContainer,false);//new ImageView(getActivity().getApplicationContext());
			String imageUrl = mContext.getString(R.string.basurl_icons)+icon;
			ImageLoader.getInstance().displayImage(imageUrl, mImageView); 
			iconContainer.addView(mImageView);
		}
		
		final NewCommentAdapter that = this;
		OnClickListener listen = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new DFragmentLoading()
  				  .show( that.fragmentManager , DFragmentLoading.TAG_DEFAULTTAG+"!");
				new BlipEntry(mContext)
				  .updateEntry(that.act , comment.getEntry_id(),DFragmentLoading.TAG_DEFAULTTAG+"!");
			}
		};
		
		convertView.setOnClickListener(listen);
		((FormattedTextView)convertView.findViewById(R.id.new_comment_item_content)).setOnClickListener(listen);
		
		
		if(C.VERBOSE){Log.d(C.TAG,"textviews set. now returning");}
		
		return convertView;
	}


}