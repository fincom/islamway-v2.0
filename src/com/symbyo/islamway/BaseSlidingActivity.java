package com.symbyo.islamway;

import android.os.Bundle;
import android.view.View;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.symbyo.islamway.fragments.SlideMenuFragment;
import com.symbyo.islamway.fragments.SlideMenuFragment.SlideMenuItem;
import com.symbyo.islamway.persistance.Repository;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public abstract class BaseSlidingActivity extends SlidingFragmentActivity
		implements
		OnSlideMenuItemClick {

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		// create the repository singleton object.
		Repository.getInstance( getApplicationContext() );

		setContentView( R.layout.base_activity_frame );

		// check if the slidingmenu_frame frame exists. if it does, then the
		// the slidemenu is always visible.
		SlidingMenu sm = getSlidingMenu();
		if ( findViewById( R.id.slidemenu_frame ) == null ) {
			setBehindContentView( R.layout.slidemenu_frame );
			sm.setSlidingEnabled( true );
			sm.setBehindWidthRes( R.dimen.slidingmenu_width );
			sm.setShadowWidthRes( R.dimen.slidemenu_shadow_width );
			sm.setShadowDrawable( R.drawable.shadow );
			sm.setTouchModeAbove( SlidingMenu.TOUCHMODE_FULLSCREEN );

			getSupportActionBar().setDisplayHomeAsUpEnabled( true );

			setSlidingActionBarEnabled( true );
		} else {
			// setting the behind view with a dummy view.
			setBehindContentView( new View( this ) );
			sm.setSlidingEnabled( false );
			sm.setTouchModeAbove( SlidingMenu.TOUCHMODE_NONE );
		}
		if ( savedInstanceState == null ) {
			getSupportFragmentManager().beginTransaction()
					.replace( R.id.slidemenu_frame, new SlideMenuFragment() )
					.commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch ( item.getItemId() ) {
			case android.R.id.home:
				toggle();
		}
		return super.onOptionsItemSelected( item );
	}

	@Override
	public void onSlideMenuItemClick( SlideMenuItem item )
	{
		slideMenuItemClicked( item );
	}

	/**
	 * Called when an item in the sliding menu is clicked.
	 *
	 * @param item
	 */
	abstract protected void slideMenuItemClicked( SlideMenuItem item );

	@Override
	protected void onDestroy()
	{
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}

}
