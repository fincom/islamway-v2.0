package com.symbyo.islamway.fragments;

import android.app.Activity;
import android.os.Bundle;
import com.symbyo.islamway.ServiceHelper;
import com.symbyo.islamway.domain.Entry;
import junit.framework.Assert;

import java.util.List;

/**
 * @author kdehairy
 * @since 2/27/13
 */
public class SubCollectionsFragment extends BaseEntryFragment {

	public final static String PARENT_KEY = "parent_key";

	private Entry mParent;

	@Override
	protected void doOnCreate( Bundle savedInstanceState )
	{
		mParent = (Entry) getArguments().getParcelable( PARENT_KEY );
		Assert.assertNotNull( mParent );
		getSherlockActivity().setTitle( mParent.getTitle() );
	}

	@Override
	protected int doRequestCollections( ServiceHelper helper )
	{
		return helper.getSubCollections( mParent );
	}

	@Override
	protected void setActivityTitle( Activity activity )
	{
		activity.setTitle( mParent.getTitle() );
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
