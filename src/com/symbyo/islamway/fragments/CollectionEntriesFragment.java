package com.symbyo.islamway.fragments;

import android.app.Activity;
import android.os.Bundle;
import com.symbyo.islamway.ServiceHelper;
import com.symbyo.islamway.domain.Entry;
import junit.framework.Assert;

import java.util.List;

/**
 * @author kdehairy
 * @since 3/3/13
 */
public class CollectionEntriesFragment extends BaseEntryFragment {
	public static final String ENTRY_KEY = "entry_key";

	private Entry mEntry;

	@Override
	protected void doOnCreate( Bundle savedInstanceState )
	{
		Bundle bndl = getArguments();
		Assert.assertNotNull( bndl );
		mEntry = (Entry) bndl.getParcelable( ENTRY_KEY );
		Assert.assertNotNull( mEntry );
		getSherlockActivity().setTitle( mEntry.getTitle() );
	}

	@Override
	protected int doRequestCollections( ServiceHelper helper )
	{
		return helper.getCollectionEntries( mEntry );
	}

	@Override
	protected void setActivityTitle( Activity activity )
	{
		activity.setTitle( mEntry.getTitle() );
	}

	/**
	 * get the collections from the database.
	 */
	@Override
	protected List<Entry> doRetrieveCollections()
	{
		// TODO implement the method body
		return null;
	}

	@Override
	protected boolean isSavedLocally()
	{
		// TODO implement the method body
		return false;
	}
}
