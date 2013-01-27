package com.symbyo.islamway;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class QuranActivity extends SlidingFragmentActivity {
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		/*if (mContent == null) {
			mContent = getSupportFragmentManager()
					.getFragment(savedInstanceState, CONTENT_TOKEN);
        }*/
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//getSupportFragmentManager().putFragment(outState, CONTENT_TOKEN, mContent);
		super.onSaveInstanceState(outState);
	}

	private int mRequestId = 0;
	private Fragment mContent;
	
	//private final String CONTENT_TOKEN = "content";

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTitle(R.string.quran);
        
        setContentView(R.layout.quran_activity_frame);
        
        // check if content frame is already loaded.
        /*if (savedInstanceState != null) {
			mContent = getSupportFragmentManager()
					.getFragment(savedInstanceState, CONTENT_TOKEN);
        }*/
		if (mContent == null) {
			mContent = new ScholarListFragment();
		}
		// load the content fragment into the frame.
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
        	@SuppressWarnings("null")
			ServiceHelper helper = ServiceHelper.getInstance(getApplicationContext());
            //mRequestId = helper.getQuranScholars();
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
}
