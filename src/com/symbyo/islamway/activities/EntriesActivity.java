package com.symbyo.islamway.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.symbyo.islamway.R;
import com.symbyo.islamway.domain.Collection;
import com.symbyo.islamway.fragments.CollectionEntriesFragment;
import com.symbyo.islamway.fragments.SlideMenuFragment;
import junit.framework.Assert;

/**
 * @author kdehairy
 * @since 3/3/13
 */
public class EntriesActivity extends BaseSlidingActivity {

	public static final String EXTRA_COLLECTION = "extra_collection";

	/**
	 * Called when an item in the sliding menu is clicked.
	 *
	 * @param item
	 * @return true if you already processed the event.
	 */
	@Override
	protected boolean slideMenuItemClicked(
			SlideMenuFragment.SlideMenuItem item )
	{
		return false;
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		Intent intent = getIntent();
		Collection collection =
				(Collection) intent.getParcelableExtra( EXTRA_COLLECTION );
		Assert.assertNotNull( collection );
		if ( savedInstanceState == null ) {
			Bundle bndl = new Bundle();
			bndl.putParcelable( CollectionEntriesFragment.COLLECTION_KEY, collection );
			Fragment content = new CollectionEntriesFragment();
			content.setArguments( bndl );
			getSupportFragmentManager().beginTransaction()
					.replace( R.id.content_frame, content )
					.commit();
		}
	}
}
