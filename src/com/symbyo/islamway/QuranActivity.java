package com.symbyo.islamway;

import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class QuranActivity extends SlidingFragmentActivity {
	
	private int mRequestId = 0;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    	
        setContentView(R.layout.activity_quran);
        setBehindContentView(R.layout.activity_quran);
        
        SlidingMenu sMenu = getSlidingMenu();
        sMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        
        // TODO: remove test code
        //TEST BEGIN
        //if (isNetworkAvailable()) {
        	@SuppressWarnings("null")
			ServiceHelper helper = ServiceHelper.getInstance(getApplicationContext());
            mRequestId = helper.getQuranScholars();
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
