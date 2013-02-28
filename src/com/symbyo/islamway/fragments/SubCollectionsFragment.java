package com.symbyo.islamway.fragments;

import android.app.Activity;
import android.os.Bundle;
import com.symbyo.islamway.ServiceHelper;
import com.symbyo.islamway.domain.Collection;
import com.symbyo.islamway.fragments.BaseCollectionFragment;
import junit.framework.Assert;

/**
 * @author kdehairy
 * @since 2/27/13
 */
public class SubCollectionsFragment extends BaseCollectionFragment {

	public final static String PARENT_KEY = "parent_key";

	private Collection mParent;

	@Override
	protected void doOnCreate( Bundle savedInstanceState )
	{
		mParent = (Collection) getArguments().getParcelable( PARENT_KEY );
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
}
