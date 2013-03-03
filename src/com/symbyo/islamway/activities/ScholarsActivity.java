package com.symbyo.islamway.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.symbyo.islamway.R;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.domain.Section;
import com.symbyo.islamway.domain.Section.SectionType;
import com.symbyo.islamway.fragments.ScholarListFragment;
import com.symbyo.islamway.fragments.Searchable;
import com.symbyo.islamway.fragments.SlideMenuFragment;
import com.symbyo.islamway.fragments.SlideMenuFragment.MenuItemType;
import com.symbyo.islamway.fragments.SlideMenuFragment.SlideMenuItem;
import com.symbyo.islamway.persistance.Repository;

public class ScholarsActivity extends BaseSlidingActivity implements
		ScholarListFragment.OnScholarItemClick {

	private final       int    REQUEST_INVALID      = 0;
	private final       String REQUEST_KEY          = "request";
	private final       String ACTIVITY_TITLE_KEY   = "title";
	public static final String EXTRA_SLIDEMENU_ITEM = "selected_item_type";

	/**
	 * This is the id of the latest request sent to the ServiceHelper.
	 */
	private int mRequestId = REQUEST_INVALID;

	/**
	 * The Activity title.
	 */
	private String mActivityTitle;

	@Override
	protected void onSaveInstanceState( Bundle outState )
	{
		outState.putInt( REQUEST_KEY, mRequestId );
		outState.putString( ACTIVITY_TITLE_KEY, mActivityTitle );
		super.onSaveInstanceState( outState );
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		mActivityTitle = getString( R.string.quran );
		if ( savedInstanceState != null ) {
			// load back the latest request id, if any
			mRequestId = savedInstanceState.getInt( REQUEST_KEY );
			// load the activity title
			mActivityTitle = savedInstanceState.getString( ACTIVITY_TITLE_KEY );
		} else {
			// load the content fragment into the frame.
			Intent intent = getIntent();
			MenuItemType type = MenuItemType.values()[intent.getIntExtra(
					EXTRA_SLIDEMENU_ITEM,
					MenuItemType.QURAN.ordinal() )];
			Section section;
			switch ( type ) {
				case QURAN:
					section = Repository.getInstance( getApplicationContext() )
							.getSection( SectionType.QURAN );
					mActivityTitle = getString( R.string.quran );
					break;
				case LESSONS:
					section = Repository.getInstance( getApplicationContext() )
							.getSection( SectionType.LESSONS );
					mActivityTitle = getString( R.string.lessons );
					break;
				default:
					section = Repository.getInstance( getApplicationContext() )
							.getSection( SectionType.QURAN );
					mActivityTitle = getString( R.string.quran );
			}
			Bundle bndl = new Bundle();
			bndl.putParcelable( ScholarListFragment.SECTION_KEY, section );
			Fragment content = new ScholarListFragment();
			content.setArguments( bndl );
			getSupportFragmentManager().beginTransaction()
					.replace( R.id.content_frame, content ).commit();
		}

		setTitle( mActivityTitle );
	}

	@Override
	public boolean onSearchRequested()
	{
		try {
			Searchable fragment = (Searchable) getSupportFragmentManager()
					.findFragmentById( R.id.content_frame );
			if ( fragment != null ) {
				fragment.expandSearchView();
			}
		} catch ( ClassCastException e ) {
			e.printStackTrace();
		}

		return super.onSearchRequested();
	}

	@Override
	protected void slideMenuItemClicked( SlideMenuItem item )
	{
		if ( item.type == SlideMenuFragment.MenuItemType.QURAN ) {
			Fragment content = new ScholarListFragment();
			Bundle bndl = new Bundle();
			Section section = Repository.getInstance( getApplicationContext() )
					.getSection( SectionType.QURAN );
			bndl.putParcelable( ScholarListFragment.SECTION_KEY, section );
			content.setArguments( bndl );
			getSupportFragmentManager().beginTransaction()
					.replace( R.id.content_frame, content )
					.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_FADE )
					.commit();
		} else if ( item.type == SlideMenuFragment.MenuItemType.LESSONS ) {
			Fragment content = new ScholarListFragment();
			Bundle bndl = new Bundle();
			Section section = Repository.getInstance( getApplicationContext() )
					.getSection( SectionType.LESSONS );
			bndl.putParcelable( ScholarListFragment.SECTION_KEY, section );
			content.setArguments( bndl );
			getSupportFragmentManager().beginTransaction()
					.replace( R.id.content_frame, content )
					.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_FADE )
					.commit();
		} else if ( item.type == SlideMenuFragment.MenuItemType.PLAYING_LIST ) {
			// TODO remove the following lines and start the playing list
			// activity
			Fragment frgmnt = getSupportFragmentManager().findFragmentById(
					R.id.content_frame );
			getSupportFragmentManager().beginTransaction().remove( frgmnt )
					.commit();
		}
		// change the title
		mActivityTitle = item.text;
		setTitle( mActivityTitle );
		showContent();
	}

	@Override
	public void onLoadScholarCollectionsClick( Scholar scholar,
											   Section section )
	{
		Intent intent = new Intent( this, CollectionsActivity.class );
		intent.putExtra( CollectionsActivity.EXTRA_SCHOLAR, scholar );
		intent.putExtra( CollectionsActivity.EXTRA_SECTION, section );

		startActivity( intent );
	}
}
