package com.symbyo.islamway.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.symbyo.islamway.R;
import com.symbyo.islamway.domain.Collection;
import com.symbyo.islamway.domain.Entry;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.domain.Section;
import com.symbyo.islamway.fragments.ScholarCollectionFragment;
import com.symbyo.islamway.fragments.SlideMenuFragment.SlideMenuItem;
import com.symbyo.islamway.fragments.SubCollectionsFragment;
import junit.framework.Assert;

/**
 * @author kdehairy
 * @since 2/13/13
 */
public class CollectionsActivity extends BaseSlidingActivity
		implements ScholarCollectionFragment.OnCollectionItemClick {
	public static final String EXTRA_SCHOLAR = "extra_key_scholar";
	public static final String EXTRA_SECTION = "extra_key_section";

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		Intent intent = getIntent();
		Scholar scholar = (Scholar) intent.getParcelableExtra( EXTRA_SCHOLAR );
		Section section = (Section) intent.getParcelableExtra( EXTRA_SECTION );

		Assert.assertNotNull( "A scholar object must be passed to the activity",
							  scholar );
		//setTitle( scholar.getName() );

		if ( savedInstanceState == null ) {
			Bundle bndl = new Bundle();
			bndl.putParcelable( ScholarCollectionFragment.SCHOLAR_KEY,
								scholar );
			bndl.putParcelable( ScholarCollectionFragment.SECTION_KEY,
								section );
			Fragment content = new ScholarCollectionFragment();
			content.setArguments( bndl );
			getSupportFragmentManager().beginTransaction()
					.replace( R.id.content_frame, content )
					.commit();
		}

	}

	@Override
	protected boolean slideMenuItemClicked( SlideMenuItem item )
	{
		return false;
	}

	@Override
	public void onCollectionItemClick(
			Collection item )
	{
		if ( item.getType() == Entry.EntryType.MUSHAF ) {

		} else if ( item.getType() == Entry.EntryType.LESSON_SERIES ) {

		} else if ( item.getType() == Entry.EntryType.GROUP ) {
			Fragment content = new SubCollectionsFragment();
			Bundle bndl = new Bundle();
			bndl.putParcelable( SubCollectionsFragment.PARENT_KEY, item );
			content.setArguments( bndl );
			getSupportFragmentManager().beginTransaction()
					.replace( R.id.content_frame, content )
					.addToBackStack( null )
					.commit();
		}
	}
}
