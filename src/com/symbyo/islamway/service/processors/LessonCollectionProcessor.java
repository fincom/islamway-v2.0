package com.symbyo.islamway.service.processors;

import android.content.Context;
import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.domain.Scholar;

/**
 * @author kdehairy
 * @since 4/9/13
 */
public class LessonCollectionProcessor extends CollectionsProcessor {

	public LessonCollectionProcessor( Context context, int resource_id )
	{
		super( context, resource_id );
	}

	@Override
	protected void updateSyncState( DomainObject obj )
	{
		try {
			Scholar scholar = (Scholar) obj;
			scholar.setLessonSyncState( DomainObject.SyncState.SYNC_STATE_FULL );
		} catch ( ClassCastException e ) {
			e.printStackTrace();
		}
	}
}
