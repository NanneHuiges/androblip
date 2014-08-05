package com.huiges.AndroBlip;

import java.io.File;

import com.huiges.AndroBlip.R;
import nl.huiges.apicaller.iAPIResultFragmentReceiver;
import nl.huiges.blipapi.BlipView;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * Main activity, roughtly based on the fragment-'example' from Google.
 * Doesn't include the preferences and upload (they are separate activities).
 * Loads the views as fragemnts, has the menu drawer and links the preferences.
 * 
 * @author Nanne Huiges
 *
 */
public class ActivityMain extends FragmentActivity implements iAPIResultFragmentReceiver, OnSharedPreferenceChangeListener{
	public static final String VIEW_ARGUMENT = "view_arg_number";
	public static final String SHOW_COMMENTS = "extra_show_comments";

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private ListView mDrawerList;   
	private ArrayAdapter<String> drawerItems;

	/**
	 * Views have ID's that can be used to link to them, 
	 * but the drawer has some entries above that, so there's
	 * an offset to get from drawer item to view item.
	 */
	private int drawerViewsOffset = 1;


	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	private SharedPreferences sharedPreferences;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		initPreferences();
		initImageLoader();
		
		setContentView(R.layout.main);
		
		initNavigationdrawer();
		initPagerAdapter(); 
	}

	private void initPreferences() {
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}

	/**
	 * Create the adapter that will return a fragment 
	 * for each of the primary sections of the app.
	 */
	private void initPagerAdapter() {
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);	
	}

	
	/***********Drawer******/
	/**
	 * Navigation bar drawer: 
	 * @see http://developer.android.com/training/implementing-navigation/nav-drawer.html
	 */
	private void initNavigationdrawer() {
		drawerItems = new ArrayAdapter<String>(this, R.layout.main_drawer_item);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		setDrawerItems();
		mDrawerList.setAdapter(drawerItems);


		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());        
		mTitle = mDrawerTitle = getTitle();

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description */
				R.string.drawer_close  /* "close drawer" description */
				) {
			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				setDrawerItems();
				getActionBar().setTitle(mTitle);
			}
			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				setDrawerItems();
				getActionBar().setTitle(mDrawerTitle);
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void setDrawerItems() {
		drawerItems.clear();
		if ( C.isLoggedIn(this) ) {			
			drawerViewsOffset = 3;
			drawerItems.add("Account: "+sharedPreferences.getString(FragmentPreference.PREFKEY_USER_NAME, "?") );
			drawerItems.add("Upload" );
			drawerItems.add("Comments");
		} else {
			drawerViewsOffset = 1;
			drawerItems.add("Log in");
		}	
		for(int i=0;i<BlipView.getCount(this);i++){
			drawerItems.add((String) BlipView.getTitleFromPosition(getApplicationContext(), i));
		}
		drawerItems.notifyDataSetChanged();
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);	 
		
		if ( position == 0 ) {
			DialogFragment fragment = new DFragmentAccount();
			addFragment( fragment, DFragmentLoading.TAG_DEFAULTTAG );
		}else{
			if ( C.isLoggedIn(this) ) {
				switch(position){
				case 1:
					Intent myIntent = new Intent(this, ActivityUpload.class);
					startActivity(myIntent);	    		
					break;
				case 2:
					DialogFragment fragment1 = new DFragmentComments();
					addFragment( fragment1, DFragmentLoading.TAG_DEFAULTTAG );
					break;
				}
			}
			if(position-drawerViewsOffset >= 0){
				mViewPager.setCurrentItem(position-drawerViewsOffset);
			}
		}
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/***********************/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(C.VERBOSE){Log.d(C.TAG, "oncreateoptionsmenu");}

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem item = menu.findItem(R.id.action_newcomments);
		boolean vis = sharedPreferences.getBoolean(FragmentPreference.PREFKEY_HASUNREADCOMMS, false);
		item.setVisible(vis);
		if(C.VERBOSE){Log.d(C.TAG, "visibility: "+(vis?"true":"false"));}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {		
			return true;
		}

		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent myIntent = new Intent(ActivityMain.this, ActivityPreferences.class);
			ActivityMain.this.startActivityForResult(myIntent, 1);// (myIntent);
			break;
		case R.id.action_newcomments:
			DialogFragment fragment1 = new DFragmentComments();
			addFragment( fragment1, DFragmentLoading.TAG_DEFAULTTAG );
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void addFragment(DialogFragment fragment, String tag) {
		fragment.show(getSupportFragmentManager(), tag);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			//maybe not always new?
			//TODO reload function
			Fragment fragment = null;
			fragment = new FragmentBlipView();
			Bundle args = new Bundle();
			args.putInt(ActivityMain.VIEW_ARGUMENT, position);
			fragment.setArguments(args);	

			mDrawerList.setItemChecked(position-drawerViewsOffset, true);	  

			return fragment;		
		}

		@Override
		public int getCount() {
			return BlipView.getCount(getApplicationContext());
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return BlipView.getTitleFromPosition(getApplicationContext(), position);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if(key.equals(FragmentPreference.PREFKEY_USEDISCCACHE)){
			initImageLoader();
		}else if(key.equals(FragmentPreference.PREFKEY_LOGGEDIN)){
			mViewPager.getAdapter().notifyDataSetChanged();
			setDrawerItems();
		}
		
	}


	/**
	 * Android universal Image Loader
	 * Create global configuration and initialize ImageLoader with this configuration
	 * 
	 * @TODO: test/fix caching size usage
	 */
	public void initImageLoader() {
		boolean useDiscCache = sharedPreferences.getBoolean("pref_usedisccache", true);
		boolean useMemCache = sharedPreferences.getBoolean("pref_usememcache", true);
		ImageLoader il = ImageLoader.getInstance();
		
		if(il.isInited()){
			il.destroy();
		}

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(useMemCache) //TODO
		.cacheOnDisc(useDiscCache)
		.showImageOnLoading(R.drawable.coffee) // resource or drawable
		.showImageForEmptyUri(R.drawable.coffee)
		.showImageOnFail(R.drawable.coffee)
		.build();    

		File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
		cacheDir.mkdirs(); // needs android.permission.WRITE_EXTERNAL_STORAGE

		//TODO max size from setting?

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
		.defaultDisplayImageOptions(defaultOptions)
		.discCache(new TotalSizeLimitedDiscCache(cacheDir, 25 * 1024 * 1024))
		.build();

		ImageLoader.getInstance().init(config);				

	}

	@Override
	protected void onResume(){
	    	super.onResume();
			if(C.VERBOSE){Log.d(C.TAG,"activitymain onresume");}

			if(    getIntent() != null 
				&& getIntent().getExtras() != null 
				&& getIntent().getExtras().getBoolean(ActivityMain.SHOW_COMMENTS)){
				DialogFragment fragment = new DFragmentComments();
				addFragment( fragment, DFragmentLoading.TAG_DEFAULTTAG );
			}
			
			//start notification if needed!
	    	//or cancel it!
			//code also in BootBroadcastRec!
	    	
	    	
	    	Boolean useNot = false;// prefs.getBoolean("use_notify_not", true);
	    	Boolean useCom = sharedPreferences.getBoolean(FragmentPreference.PREFKEY_NOTIFYCOMMENT, true); 
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	    	
			if(!(useNot || useCom)){
				Intent i=new Intent(this, CommentService.class);
				PendingIntent pi=PendingIntent.getService(this, 0, i, 0);
				pi.cancel();
				mNotificationManager.cancel(BlipNotifications.COM_ID);			
				mNotificationManager.cancel(BlipNotifications.NOT_ID);
			}else{
				if(!useNot){			
					mNotificationManager.cancel(BlipNotifications.NOT_ID);
				}
				if(!useCom){
					mNotificationManager.cancel(BlipNotifications.COM_ID);
				}
				
				
				
				if(C.isLoggedIn(this)){
					if(C.VERBOSE){Log.d(C.TAG,"doe alarm");}

					AlarmManager mgr=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
					Intent i=new Intent(this, CommentService.class);
					PendingIntent pi=PendingIntent.getService(this, 0, i, 0);
					
					long interval = C.getNotifyFrequency(getApplicationContext());
					
					mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
							SystemClock.elapsedRealtime(),
							interval, 
							pi);
				}
			}	
	    }
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			mSectionsPagerAdapter.notifyDataSetChanged();
			setDrawerItems();
		}
	}

	@Override
	public void showError(CharSequence message) {
		 Fragment prev = getSupportFragmentManager().findFragmentByTag(DFragmentLoading.TAG_DEFAULTTAG);
		if (prev != null) {
			DialogFragment df = (DialogFragment) prev;
			df.dismiss();
		}	

		Toast t = Toast.makeText(this, message , Toast.LENGTH_LONG);
		View view = t.getView();
		view.setBackgroundColor(Color.argb(150, 255, 0, 0));
		t.show();
	}
}
