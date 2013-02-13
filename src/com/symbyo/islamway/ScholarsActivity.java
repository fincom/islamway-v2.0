package com.symbyo.islamway;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.domain.Section;
import com.symbyo.islamway.domain.Section.SectionType;
import com.symbyo.islamway.fragments.ScholarListFragment;
import com.symbyo.islamway.fragments.SlideMenuFragment;
import com.symbyo.islamway.fragments.SlideMenuFragment.SlideMenuItem;
import com.symbyo.islamway.persistance.Repository;

public class ScholarsActivity extends BaseSlidingActivity implements
        ScholarListFragment.OnScholarItemClick {

	private final int		REQUEST_INVALID		= 0;
	private final String	REQUEST_KEY			= "request";
	private final String	ACTIVITY_TITLE_KEY	= "title";

	/**
	 * This is the id of the latest request sent to the ServiceHelper.
	 */
	private int				mRequestId			= REQUEST_INVALID;

	/**
	 * The Activity title.
	 */
	private String			mActivityTitle;

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

		if ( savedInstanceState != null ) {
			// load back the latest request id, if any
			mRequestId = savedInstanceState.getInt( REQUEST_KEY );
			// load the activity title
			mActivityTitle = savedInstanceState.getString( ACTIVITY_TITLE_KEY );
			if ( mActivityTitle == null ) {
				mActivityTitle = getString( R.string.quran );
			}
		} else {
			mActivityTitle = getString( R.string.quran );
			// load the content fragment into the frame.
			Bundle bndl = new Bundle();
			Section section = Repository.getInstance( getApplicationContext() )
					.getSection( SectionType.QURAN );
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
	}

    @Override
    public void onScholarItemClick( Scholar scholar )
    {
        Intent intent = new Intent(this,  QuranCollectionsActivity.class );
        intent.putExtra( QuranCollectionsActivity.EXTRA_SCHOLAR, scholar );
        startActivity( intent );
    }
}
