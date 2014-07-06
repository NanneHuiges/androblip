package com.huiges.AndroBlip;

import java.util.Iterator;

import com.huiges.AndroBlip.views.FormattedTextView;
import com.huiges.AndroBlip.views.ResizeImageView;
import com.huiges.AndroBlip.R;
import nl.huiges.apicaller.APICaller;
import nl.huiges.apicaller.iAPIResultFragment;
import nl.huiges.apicaller.iAPIResultFragmentReceiver;
import nl.huiges.apicaller.iAPIResultReceiver;
import nl.huiges.blipapi.BlipEntry;
import nl.huiges.blipapi.BlipPostComment;
import nl.huiges.blipapi.EntryCommentWAO;
import nl.huiges.blipapi.EntryWAO;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Fragment for blipfoto entry.
 * Based on dialog, so behaves as 'popup'
 *
 */
public class DFragmentEntry extends DialogFragment implements iAPIResultFragment, iAPIResultReceiver{
	private EntryWAO entry;
	private ScrollView mainView;
	private ResizeImageView imageView; 
	private FormattedTextView dateView;
	private FormattedTextView journalTitleView;
	private FormattedTextView titleView;
	private FormattedTextView descrView;
	private FormattedTextView addComment;
	private FormattedTextView showComments;
	private ImageView favourite;
	private ImageView subscribe;

	private LayoutInflater inflater;
	private ViewGroup container;
	protected long eventTime;
	private String dismissableFragmentTag;
	private ProgressBar favspinner;
	private ProgressBar subspinner;
	private LinearLayout commentContainer;

	private final static int SIGNAL_POST = 0;
	private final static int SIGNAL_FAVO = 1;
	private static final int SIGNAL_SUBS = 2;
	
	public void setDismissTag(String disTag){
		dismissableFragmentTag = disTag;
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(C.VERBOSE){Log.d(C.TAG,"onCreateView dfragmententry");}
		setRetainInstance(true);
		this.inflater = inflater;
		this.container = container;
		setViews();
		fillViews();	
		addListeners();
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		removeOtherDiags();
		return mainView;
	}

	@Override
	public void onResume(){
		super.onResume();
		if(C.VERBOSE){Log.d(C.TAG,"onResume");}

	}
	
	@Override
	public void onDestroyView() {
	  if (getDialog() != null && getRetainInstance())
	    getDialog().setOnDismissListener(null);
	  super.onDestroyView();
	}
	
	private void removeOtherDiags(){
		 Fragment prev; 
		 prev  = getActivity().getSupportFragmentManager().findFragmentByTag(DFragmentLoading.TAG_DEFAULTTAG);
		if (prev != null) {
			DialogFragment df = (DialogFragment) prev;
			df.dismiss();
		}
		
		if(dismissableFragmentTag != null){
			prev = getActivity().getSupportFragmentManager().findFragmentByTag(dismissableFragmentTag);
			if (prev != null) {
				DialogFragment df = (DialogFragment) prev;
				df.dismiss();
			}
		}
	}

	private void addListeners(){
		showComments.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				((View) v).setClickable(false);
				int alpha = 50;
				((TextView) v ).setTextColor(Color.argb(alpha, 0, 0, 0));
				
				LinearLayout commentContainer = (LinearLayout) mainView.findViewById(R.id.entry_comment_container);
				for (EntryCommentWAO comment : entry.getComments()) {
					showComment(comment, commentContainer, null);
				}
				mainView.post(new Runnable(){
					@Override
					public void run() {
						mainView.smoothScrollTo(0, mainView.findViewById(R.id.separator_context_comments).getBottom());
					}
				});
			}
		});

		final DFragmentEntry that = this;

		addComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((View) v).setClickable(false);
				int alpha = 50;
				((TextView) v ).setTextColor(Color.argb(alpha, 0, 0, 0));
				
				final LinearLayout inflatedView = (LinearLayout) inflater.inflate(R.layout.entry_comment_add, container, false);
				((FormattedTextView) inflatedView.findViewById(R.id.entry_add_comment_post)).setText("add");
				commentContainer.addView(inflatedView,0);

				inflatedView.findViewById(R.id.entry_add_comment_post).setOnClickListener(new OnClickListener() {


					@Override
					public void onClick(View v) {
						((View) v).setVisibility(View.GONE);//get that out of the way for doubleclickers
						//TODO check for fileuri?
						BlipPostComment bpc = new BlipPostComment(getActivity());
						Bundle extras =  new Bundle();
						extras.putString("entry_id", that.entry.getEntry_id() );
						//extras.putString("reply_to_comment_id", ((EditText) getActivity().findViewById(R.id.formDescr)).getText().toString() );
						extras.putString("comment", ((EditText) inflatedView.findViewById(R.id.formComment)).getText().toString() );

						bpc.PostComment(extras, that, SIGNAL_POST);


					}
				});


				EditText commentForm = (EditText) mainView.findViewById(R.id.formComment);
				commentForm.requestFocus();
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(commentForm, InputMethodManager.SHOW_IMPLICIT);
				mainView.post(new Runnable(){
					@Override
					public void run() {
						mainView.smoothScrollTo(0, mainView.findViewById(R.id.separator_context_comments).getBottom());
					}
				});
			}
		});

		imageView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(C.VERBOSE){Log.d(C.TAG,"ontouch entry");}
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					if(C.VERBOSE){Log.d(C.TAG,"ontouch down");}

					eventTime = event.getEventTime();
					return true;
				case MotionEvent.ACTION_MOVE:
					if(C.VERBOSE){Log.d(C.TAG,"ontouch move");}
					return false;//whatever, ignore					
				case MotionEvent.ACTION_UP:
					if(C.VERBOSE){Log.d(C.TAG,"ontouch up");}

					if(event.getEventTime() - eventTime < 1000){
						if(C.VERBOSE){Log.d(C.TAG,"ontouch quick");}

						DisplayMetrics dm = new DisplayMetrics();
						getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
						int wPix = dm.widthPixels;
						DFragmentLoading dfl;
						if(event.getX() > wPix/2){
							//Toast.makeText(getActivity().getApplicationContext(), "NEXT", Toast.LENGTH_LONG).show();

							//ViewItemWAO bvi = ;// 
							if(entry.getNext() != null){
								if(C.VERBOSE){Log.d(C.TAG,"next: "+entry.getNext());}
								dfl = new DFragmentLoading();
								dfl.show(that.getActivity().getSupportFragmentManager(), DFragmentLoading.TAG_DEFAULTTAG);
								(new BlipEntry(getActivity().getApplicationContext()))
								.updateEntry((iAPIResultFragmentReceiver) getActivity(),entry.getNext(),DFragmentLoading.TAG_ENTRYTAGPREFIX+that.entry.getEntry_id());
								return false;
							}
							return true;
						}else{
							if(entry.getPrev() != null){
								dfl = new DFragmentLoading();
								dfl.show(that.getActivity().getSupportFragmentManager(), DFragmentLoading.TAG_DEFAULTTAG);

								(new BlipEntry(getActivity().getApplicationContext()))
								.updateEntry((iAPIResultFragmentReceiver) getActivity(),entry.getPrev(),DFragmentLoading.TAG_ENTRYTAGPREFIX+that.entry.getEntry_id());
								return false;
							}
							return true;						
						}
					}
					return false ;//we're done
				case MotionEvent.ACTION_CANCEL:
					if(C.VERBOSE){Log.d(C.TAG,"ontouch cancel");}

					return false;
				}
				return false;
			}

		});
		
		favourite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(C.VERBOSE){Log.d(C.TAG,"favourite");}
				favourite.setVisibility(View.GONE);
				favspinner.setVisibility(View.VISIBLE);
				if ( ! entry.favourite(getActivity().getApplicationContext(), that,SIGNAL_FAVO) ){
					favourite.setImageResource(R.drawable.cross);
					favourite.setVisibility(View.VISIBLE);
					favspinner.setVisibility(View.GONE);
				}
			}
		});
		
		subscribe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(C.VERBOSE){Log.d(C.TAG,"subscribe");}				
				subscribe.setVisibility(View.GONE);
				subspinner.setVisibility(View.VISIBLE);
				if( ! entry.subtoggle(getActivity().getApplicationContext(), that,SIGNAL_SUBS)){
					subscribe.setImageResource(R.drawable.cross);
					subscribe.setVisibility(View.VISIBLE);
					subspinner.setVisibility(View.GONE);
				}
			}
		});
	}


	private void showComment(final EntryCommentWAO comment, ViewGroup localContainer, Integer replyToId){
		final Integer mReplyToId;
		if(comment.getReply() == 1 ){
			mReplyToId = comment.getComment_id();
		}else{
			mReplyToId = replyToId;
		}

		final LinearLayout inflatedComment = (LinearLayout) inflater.inflate(R.layout.entry_comment_item, localContainer, false);
		((FormattedTextView) inflatedComment.findViewById(R.id.entry_comment_item_content)).setText(comment.getContentSpanned());	
		((TextView) inflatedComment.findViewById(R.id.entry_comment_item_user)).setText(comment.getDisplay_nameSpanned());

		LinearLayout iconContainer = (LinearLayout) inflatedComment.findViewById(R.id.entry_comment_iconcontainer);
		for (String icon : comment.getIcons()){
			ImageView mImageView = (ImageView) inflater.inflate(R.layout.user_icon,iconContainer,false);//new ImageView(getActivity().getApplicationContext());
			String imageUrl = "http://www.blipfoto.com/_images/user_icons/"+icon;
			ImageLoader.getInstance().displayImage(imageUrl, mImageView); 
			iconContainer.addView(mImageView);
		}

		LinearLayout replyContainer = (LinearLayout) inflater.inflate(R.layout.entry_comment_reply_container, localContainer, false);
		if(comment.getReplies() == null){
			if(C.VERBOSE){Log.d(C.TAG,"null");}
		}

		for(Iterator<EntryCommentWAO> i = comment.getReplies().iterator(); i.hasNext(); ) {
			EntryCommentWAO reply = i.next();
			if(i.hasNext()){
				showComment(reply,replyContainer, null ); //show reply button on the next comment
			}else{
				showComment(reply,replyContainer, mReplyToId );
			}
		}
		inflatedComment.addView(replyContainer);
		final DFragmentEntry that = this;
		if(mReplyToId != null && comment.getReplies().isEmpty()){
			FormattedTextView addreply = new FormattedTextView(getActivity().getApplicationContext());
			addreply.setText("reply");
			addreply.setGravity(Gravity.RIGHT);
			addreply.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final LinearLayout inflatedView = (LinearLayout) inflater.inflate(R.layout.entry_comment_add, container, false);
					((FormattedTextView) inflatedView.findViewById(R.id.entry_add_comment_post)).setText("add");
					inflatedComment.addView(inflatedView);

					inflatedView.findViewById(R.id.entry_add_comment_post).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							((View) v).setVisibility(View.GONE);//get that out of the way for doubleclickers
							//TODO check for fileuri?
							BlipPostComment bpc = new BlipPostComment(getActivity());
							Bundle extras =  new Bundle();
							extras.putString("reply_to_comment_id", mReplyToId.toString());
							extras.putString("comment", ((EditText) inflatedView.findViewById(R.id.formComment)).getText().toString() );
							bpc.PostComment(extras, that, SIGNAL_POST);
							inflatedView.setVisibility(View.GONE);
						}
					});


					EditText commentForm = (EditText) inflatedComment.findViewById(R.id.formComment);
					commentForm.requestFocus();
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(commentForm, InputMethodManager.SHOW_IMPLICIT);

				}
			});


			LinearLayout addReplyContainer = (LinearLayout) inflatedComment.findViewById(R.id.entry_comment_replycontainer);

			addReplyContainer.addView(addreply);
		}

		localContainer.addView(inflatedComment);
	}

	/**
	 * Assign content from the entry to the views.
	 */
	private void fillViews(){
		setImage( imageView );
		dateView        .setText( entry.getDate() );
		journalTitleView.setText( entry.getJournal_title() );
		titleView       .setText( entry.getTitle() );
		descrView       .setText( entry.getDescriptionSpanned() );
		descrView.setMovementMethod(LinkMovementMethod.getInstance());

		if(entry.canComment()){
			addComment.setText("Add Comment");
		}else{
			addComment.setText("Can't comment");
		}

		int numCom = entry.getCommentsCount();
		if(  numCom == 1 ){
			showComments.setText("Show 1 comment");
		}else if (numCom > 1 ){
			showComments.setText("Show "+numCom+" comments");
		}else{
			showComments.setText("No comments");
		}
		
		if( entry.canFavourite() ){
			favourite.setVisibility(View.VISIBLE);
			favourite.setImageResource(R.drawable.favourite);
		}
		if( entry.canSubscribe() ){
			subscribe.setImageResource(R.drawable.subscribe);
			subscribe.setVisibility(View.VISIBLE);
		}else if (entry.canUnSubscribe()  ){
			subscribe.setImageResource(R.drawable.unsubscribe);
			subscribe.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Set the correct Views for the local variables.
	 * Inflates the xml and finds the correct views by id.
	 * 
	 * @param inflater
	 * @param container
	 */
	private void setViews(){	    
		mainView         = (ScrollView) inflater.inflate(R.layout.frag_dialog_entry, container, false);

		imageView        = (ResizeImageView)   mainView.findViewById(R.id.entry_image);
		dateView         = (FormattedTextView) mainView.findViewById(R.id.entry_date);
		journalTitleView = (FormattedTextView) mainView.findViewById(R.id.entry_journal_title);
		titleView        = (FormattedTextView) mainView.findViewById(R.id.entry_title);
		descrView        = (FormattedTextView) mainView.findViewById(R.id.entry_desc);

		addComment		 = (FormattedTextView) mainView.findViewById(R.id.entry_add_comment);
		showComments	 = (FormattedTextView) mainView.findViewById(R.id.entry_show_comments);
		
		favourite		 = (ImageView) mainView.findViewById(R.id.entry_favourite);
		favspinner		 =  (ProgressBar) mainView.findViewById(R.id.entry_favspin);
		subscribe		 =  (ImageView) mainView.findViewById(R.id.entry_subscribe);
		subspinner		 =  (ProgressBar) mainView.findViewById(R.id.entry_subspin);
		
		commentContainer = (LinearLayout) mainView.findViewById(R.id.entry_comment_container);
	}


	@Override
	public void showFromResult(iAPIResultFragmentReceiver activity, String result) {
		if(C.VERBOSE){Log.d(C.TAG,"Going to create from JSON: "+result);}

		entry = new EntryWAO();
		try {
			entry.entryFromJSONString(result);
		} catch (JSONException e) {
			if(C.VERBOSE){Log.d(C.TAG,"Could not create from JSON: "+result);}
		}
		activity.addFragment(this, DFragmentLoading.TAG_ENTRYTAGPREFIX+entry.getEntry_id());
		//this.show(activity.getSupportFragmentManager(),DFragmentLoading.TAG_ENTRYTAGPREFIX+entry.getEntry_id());
	}

	/**
	 * Show the image from the entry if available.
	 * Sets image sizes, uses the imageloader, etc.
	 * @param imageView
	 */
	private void setImage(ResizeImageView imageView){
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		imageView.setMaxHeight(height);
		imageView.setMaxWidth(width);

		String imageUrl = entry.getImage();

		ImageLoader.getInstance().displayImage(imageUrl, imageView); // Default options will be used

	}

	@Override
	public void signal(int signalId, Bundle extras) {
		String resultS = extras.getString(APICaller.RESULT);
		switch (signalId){
		case SIGNAL_POST:
			if(C.VERBOSE){Log.d(C.TAG,"postcomment result: "+resultS);}
			mainView.findViewById(R.id.entry_comment_add).setVisibility(View.GONE);
			mainView.removeView(mainView.findViewById(R.id.entry_comment_add));
			JSONObject data = null;
			try {
				data = new JSONObject(resultS).getJSONObject("data");
				EntryCommentWAO comment = EntryCommentWAO.entryFromJSONObject(data);
				showComment(comment, commentContainer, null);
				mainView.post(new Runnable(){
					@Override
					public void run() {
						mainView.smoothScrollTo(0, commentContainer.getBottom());
					}
				});

			} catch (JSONException e) {
				if(C.VERBOSE){Log.d(C.TAG,"postcomment error: "+e);}
			}			
			break;
		case SIGNAL_SUBS:
			if(C.VERBOSE){Log.d(C.TAG,"favourite result: " + resultS);}
			subscribe.setImageResource(R.drawable.check);
			subscribe.setVisibility(View.VISIBLE);
			subspinner.setVisibility(View.GONE);
			break;
		case SIGNAL_FAVO:
			if(C.VERBOSE){Log.d(C.TAG,"favourite result: " + resultS);}
			favourite.setImageResource(R.drawable.check);
			favourite.setVisibility(View.VISIBLE);
			favspinner.setVisibility(View.GONE);
			break;
		}
	}
}