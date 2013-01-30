package com.symbyo.islamway;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.symbyo.islamway.fragments.ScholarListFragment;
import com.symbyo.islamway.fragments.SlideMenuFragment;
import com.symbyo.islamway.fragments.SlideMenuFragment.SlideMenuItem;
import com.symbyo.islamway.service.IWService.Section;

public class QuranActivity extends SlidingFragmentActivity implements OnSlideMenuItemClick {
	
	private final int REQUEST_INVALID = 0;
	private final String REQUEST_KEY = "request";
	private final String ACTIVITY_TITLE_KEY = "title";
	private final String LOADED_SECTION = "loaded_section";
	
	/**
	 * This is the id of the latest request sent to the ServiceHelper.
	 */
	private int mRequestId = REQUEST_INVALID;
	
	/**
	 * The fragment that holds the ScholarListFragment.
	 */
	private Fragment mContent;
	
	/**
	 * The Activity title.
	 */
	private String mActivityTitle;
	
	/**
	 * The currently loaded section in the scholars list fragment.
	 */
	private Section mLoadedSection;
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(REQUEST_KEY, mRequestId);
		outState.putString(ACTIVITY_TITLE_KEY, mActivityTitle);
		outState.putInt(LOADED_SECTION, mLoadedSection.ordinal());
		super.onSaveInstanceState(outState);
	}

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        if (savedInstanceState != null) {
        	// load back the latest request id, if any
        	mRequestId = savedInstanceState.getInt(REQUEST_KEY);
        	// load the activity title
        	mActivityTitle = savedInstanceState.getString(ACTIVITY_TITLE_KEY);
        	if (mActivityTitle == null) {
        		mActivityTitle = getString(R.string.quran);
        	}
        	mLoadedSection = Section.values()[savedInstanceState.getInt(LOADED_SECTION)];
        } else  {
        	mActivityTitle = getString(R.string.quran);
        	mLoadedSection = Section.QURAN;
        }
        
        setTitle(mActivityTitle);
        
        setContentView(R.layout.quran_activity_frame);
        
		if (mContent == null) {
			mContent = new ScholarListFragment();
		}
		// load the content fragment into the frame.
		Bundle bndl = new Bundle();
		bndl.putInt(ScholarListFragment.SECTION_KEY, mLoadedSection.ordinal());
		mContent.setArguments(bndl);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, mContent)
		.commit();
		
		// check if the slidingmenu_frame frame exists. if it does, then the 
		// the slidemenu is always visible.
		SlidingMenu sm = getSlidingMenu();
		if (findViewById(R.id.slidemenu_frame) == null) {
			setBehindContentView(R.layout.slidemenu_frame);
			sm.setSlidingEnabled(true);
			sm.setBehindWidthRes(R.dimen.slidingmenu_width);
	        sm.setShadowWidthRes(R.dimen.slidemenu_shadow_width);
			sm.setShadowDrawable(R.drawable.shadow);
	        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	        
	        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	        
	        setSlidingActionBarEnabled(true);
		} else {
			// setting the behinde view with a dummy view.
			setBehindContentView(new View(this));
			sm.setSlidingEnabled(false);
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
        
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.slidemenu_frame, new SlideMenuFragment())
		.commit();
        
        
        
        // TODO: remove test code
        //TEST BEGIN
        //if (isNetworkAvailable()) {
        	//@SuppressWarnings("null")
			/*ServiceHelper helper = ServiceHelper.getInstance(getApplicationContext());
			if (helper.getRequestState(mRequestId) == RequestState.NOT_REGISTERED) {
				mRequestId = helper.getQuranScholars();
			}*/
            
        //}
        //TEST END
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	getSupportMenuInflater().inflate(R.menu.activity_quran, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case android.R.id.home:
    		toggle();
    	}
    	return super.onOptionsItemSelected(item);
    }
    /*
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) 
          getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
    */

	@Override
	public void onSlideMenuItemClick(SlideMenuItem item) {
		// TODO display lessons scholars.
		String msg = String.format("selected item: %s", item.text);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		
		if (item.type == SlideMenuFragment.MenuItemType.QURAN) {
			mLoadedSection = Section.QURAN;
		} else if (item.type == SlideMenuFragment.MenuItemType.LESSONS) {
			mLoadedSection = Section.LESSONS;
		} else if (item.type == SlideMenuFragment.MenuItemType.PLAYING_LIST) {
			// FIXME what section to load here?!!!
		}
		
		// change the title
		mActivityTitle = item.text;
		setTitle(mActivityTitle);
		
		// load the content fragment into the frame.
		mContent = new ScholarListFragment();
		Bundle bndl = new Bundle();
		bndl.putInt(ScholarListFragment.SECTION_KEY, mLoadedSection.ordinal());
		mContent.setArguments(bndl);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, mContent)
		.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
		.commit();
	}
}
