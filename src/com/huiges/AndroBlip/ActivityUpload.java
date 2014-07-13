package com.huiges.AndroBlip;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import nl.huiges.apicaller.iAPIResultReceiver;
import nl.huiges.blipapi.BlipPostEntry;
import nl.huiges.blipapi.BlipPostImage;
import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huiges.AndroBlip.views.FormattedTextView;

/**
 * Upload an image. Receives and handles the API's result after the call.
 * 
 * @author Nanne Huiges
 *
 */
public class ActivityUpload extends FragmentActivity implements iAPIResultReceiver, OnDateSetListener {
	private Uri fileUri;
	private String altDate = null;
	
	private boolean posted = false;

	private static final int IMAGE_PICK = 1;
	private static final int IMAGE_TAKE = 2;
	
	private static final int SIGNAL_POST     = 1;
	private static final int SIGNAL_UPLOADED = 2;
	
	SharedPreferences prefs;
	AutoSaver autoSaver;

	View slideoutView;
	AnimationListener animationOutListener;
	protected DFragmentLoading loadingFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		autoSaver = new AutoSaver(prefs);
		
		setContentView(R.layout.activity_upload);
		getImageFromIntent();
		setListeners();
		autoSaver.showAutoSave(findViewById(R.id.upload_form));
		
		((TextView)findViewById(R.id.currentAction)).setText("Select or take a picture");
		
	}



	private void setListeners(){
		final ActivityUpload that = this;

		//choose from gallery
		((Button) findViewById(R.id.choose_picture))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(C.isLoggedIn(that)){
					Intent myIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(myIntent, IMAGE_PICK);
				}
			}
		});	

		//make picture
		((Button) findViewById(R.id.make_picture))
		.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// create Intent to take a picture and return control to the calling application
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				fileUri = Uri.fromFile(getOutputMediaFile());; // create a file to save the image
				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
				startActivityForResult(intent, IMAGE_TAKE);
			}

		});	

		//upload blip
		((Button) findViewById(R.id.button_sendblip))
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View button) {
				//((Button) button).setVisibility(View.GONE);//get that out of the way for doubleclickers
				disableEnableControls(false, (ViewGroup) findViewById(R.id.upload_form));

				loadingFragment = new DFragmentLoading();
				loadingFragment.show(getSupportFragmentManager(), DFragmentLoading.TAG_DEFAULTTAG);

				//TODO check for fileuri?
				BlipPostEntry bpe = new BlipPostEntry(that);
				Bundle extras =  new Bundle();
				extras.putString("title", ((EditText) findViewById(R.id.formTitle)).getText().toString() );
				extras.putString("description", ((EditText) findViewById(R.id.formDescr)).getText().toString() );
				extras.putString("tags", ((EditText) findViewById(R.id.formTags)).getText().toString() );
				if ( altDate != null){
					extras.putString("date", altDate );
				}

				bpe.postEntryData(extras, that, SIGNAL_POST);
			}

		});	

		animationOutListener = new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				slideoutView.setVisibility(View.GONE);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationStart(Animation animation) {}
		};


	}

	private void disableEnableControls(boolean enable, ViewGroup vg){
		for (int i = 0; i < vg.getChildCount(); i++){
			View child = vg.getChildAt(i);
			child.setEnabled(enable);
			if (child instanceof ViewGroup){ 
				disableEnableControls(enable, (ViewGroup)child);
			}
		}
	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DFragementDatePicker();
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}

	/**
	 * handle results from activities like pick an image (IMAGE_PICK)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
		//super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		if (resultCode == RESULT_CANCELED){
			return;
		} else if (resultCode != Activity.RESULT_OK) {
			return;
		} 


		switch(requestCode){
		case IMAGE_PICK:
			//If using pick, we need to get the file-uri from the picked image
			fileUri = imageReturnedIntent.getData();
			break;
		case IMAGE_TAKE:			
			//if we 'take' the image, the fileuri has been set before taking it, so it's allready there
			break;
		default:
			return;
		}
		setImage();
	}

	private void getImageFromIntent() {
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if (type.startsWith("image/")) {
				Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
				if (imageUri != null) {
					fileUri = imageUri;
					setImage();
				}
			}
		}
	}	

	private void setImage(){
		ImageView imageView = (ImageView) findViewById(R.id.preview_image);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		imageView.setMaxHeight(height);
		imageView.setMaxWidth(width);		

		imageView.setImageURI(fileUri);

		findViewById(R.id.wrap_prev_img).setVisibility(View.VISIBLE);
		//findViewById(R.id.button_sendblip).setVisibility(View.VISIBLE);
		
		findViewById(R.id.uploadProgress).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.currentAction)).setText("Uploading picture....");

		
		//TODO check for fileuri?
		BlipPostImage bpi = new BlipPostImage(this);
		bpi.PostEntry(fileUri, this, SIGNAL_UPLOADED);
		
	}

	private static File getOutputMediaFile(){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		//http://developer.android.com/guide/topics/media/camera.html#saving-media

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.US).format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator +
				"IMG_"+ timeStamp + ".jpg");
		return mediaFile;
	}


	@Override
	public void signal(int signalId, Bundle extras) {
		switch (signalId){
		case SIGNAL_UPLOADED:			
			Bundle parsedExtras = BlipPostImage.parseResult(extras);
			if(parsedExtras.getBoolean("error",true)){
				((ImageView)findViewById(R.id.preview_image))
					.setVisibility(View.GONE);
				showError(parsedExtras.getString("resultText","Image not uploaded") );
			}else{
				findViewById(R.id.uploadProgress).setVisibility(View.GONE);
				findViewById(R.id.button_sendblip).setVisibility(View.VISIBLE);
				((TextView)findViewById(R.id.currentAction))
					.setText("Image Uploaded, ready to blip");				
			}	
			break;
		case SIGNAL_POST:
			FormattedTextView resultV = (FormattedTextView) findViewById(R.id.postEntryResult);
			Bundle parsedExtras1 = BlipPostEntry.parseResult(extras);
			resultV.setText(parsedExtras1.getString("resultText","Unknown result"));
			resultV.setVisibility(View.VISIBLE);
			//als error, dismiss en go back, re-enable stufs, scroll to terror.
			if(parsedExtras1.getBoolean("error",true)){
				showError(parsedExtras1.getString("resultText","Unknown result"));
			}else{
				if(C.VERBOSE){Log.d(C.TAG,"niet error "+parsedExtras1.getString("resultText","Unknown result"));}
				if (!(loadingFragment == null) ){
					loadingFragment.dismiss();	
				}
				autoSaver.clearAutoSave();
				posted = true;
				finish();//TODO: load default acitivty?
			}
			break;
		}
	}

	@Override
	public void showError(CharSequence message) {
		if(C.VERBOSE){Log.e(C.TAG,"showError: error = "+message);}
		findViewById(R.id.uploadProgress).setVisibility(View.GONE);
		FormattedTextView resultV = (FormattedTextView) findViewById(R.id.postEntryResult);
		resultV.setText(message);
		resultV.setVisibility(View.VISIBLE);
				
		disableEnableControls(true, (ViewGroup) findViewById(R.id.upload_form));
		
		if (!(loadingFragment == null) ){
			loadingFragment.dismiss();	
		}
	
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		((FormattedTextView) findViewById(R.id.formDate)).setText("Custom date set, using: "+year+"-"+monthOfYear+1+"-"+dayOfMonth);
		Calendar cal = Calendar.getInstance();
		cal.set(year, monthOfYear, dayOfMonth);
		Date date = cal.getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
		altDate = df.format(date);

	}

	@Override
	protected void onPause(){
		super.onPause();
		if( ! posted ) {
			autoSaver.doAutoSave(findViewById(R.id.upload_form));
		}
	}




}
