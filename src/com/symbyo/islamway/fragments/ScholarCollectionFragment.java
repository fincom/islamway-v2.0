package com.symbyo.islamway.fragments;

import android.app.Activity;
import android.os.Bundle;
import com.symbyo.islamway.ServiceHelper;
import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.domain.Entry;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.domain.Section;
import junit.framework.Assert;

import java.util.List;

/**
 * @author kdehairy
 * @since 2/20/13
 */
public class ScholarCollectionFragment extends BaseEntryFragment {

	public final static String SCHOLAR_KEY = "scholars_key";
	public static final String SECTION_KEY = "section_key";


	private Scholar mScholar;
	private Section mSection;

	@Override
	protected void doOnCreate( Bundle savedInstanceState )
	{
		Bundle bndl = getArguments();
		Assert.assertNotNull( bndl );
		mScholar = (Scholar) bndl.getParcelable( SCHOLAR_KEY );
		mSection = (Section) bndl.getParcelable( SECTION_KEY );
		Assert.assertNotNull( mScholar );
		getSherlockActivity().setTitle( mScholar.getName() );
	}

	@Override
	protected int doRequestCollections( ServiceHelper helper )
	{
		return helper.getScholarCollection( mScholar, mSection );
	}

	@Override
	protected void setActivityTitle( Activity activity )
	{
		activity.setTitle( mScholar.getName() );
	}

	/**
	 * get the collections from the database.
	 */
	@Override
	protected List<Entry> doRetrieveCollections()
	{
		if ( mScholar == null ) {
			return null;
		}
		return mScholar.getCollectionsBySection( mSection );
	}

	@Override
	protected boolean isSavedLocally()
	{
		DomainObject.SyncState state;
		if ( mSection.getType().equals( Section.SectionType.QURAN ) ) {
			state = mScholar.getQuranSyncState();
		} else {
			state = mScholar.getLessonsSyncState();
		}

		if ( DomainObject.SyncState.SYNC_STATE_FULL.equals( state ) ) {
			return true;
		}
		return false;
	}
}
