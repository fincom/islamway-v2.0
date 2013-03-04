package com.symbyo.islamway.fragments;

import android.app.Activity;
import android.os.Bundle;
import com.symbyo.islamway.ServiceHelper;
import com.symbyo.islamway.domain.Collection;
import junit.framework.Assert;

/**
 * @author kdehairy
 * @since 3/3/13
 */
public class CollectionEntriesFragment extends BaseEntryFragment {
	public static final String COLLECTION_KEY = "collection_key";

	private Collection mCollection;

	@Override
	protected void doOnCreate( Bundle savedInstanceState )
	{
		Bundle bndl = getArguments();
		Assert.assertNotNull( bndl );
		mCollection = (Collection) bndl.getParcelable( COLLECTION_KEY );
		Assert.assertNotNull( mCollection );
		getSherlockActivity().setTitle( mCollection.getTitle() );
	}

	@Override
	protected int doRequestCollections( ServiceHelper helper )
	{
		return helper.getCollectionEntries( mCollection );
	}

	@Override
	protected void setActivityTitle( Activity activity )
	{
		activity.setTitle( mCollection.getTitle() );
	}
}
