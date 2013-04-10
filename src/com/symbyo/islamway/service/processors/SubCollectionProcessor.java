package com.symbyo.islamway.service.processors;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import com.symbyo.islamway.Utils;
import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.domain.Entry;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.persistance.Repository;
import com.symbyo.islamway.persistance.UnitOfWork;
import com.symbyo.islamway.persistance.mappers.EntryMapper;
import com.symbyo.islamway.persistance.mappers.ScholarMapper;

import java.util.List;

/**
 * @author kdehairy
 * @since 4/9/13
 */
public class SubCollectionProcessor extends CollectionsProcessor {

	public SubCollectionProcessor( Context context, int resource_id )
	{
		super( context, resource_id );
	}

	@Override
	protected void doProcess(
			List<? extends DomainObject> collection, SQLiteDatabase db,
			Intent pIntent )
	{
		Utils.FormatedLog( "processing %d collections", collection.size() );
		Utils.FormatedLog( "resource id: %d", mResourceId );
		EntryMapper mapper = (EntryMapper) Repository.getInstance()
				.getMapper( Entry.class );
		Entry parent = mapper.findEntryByServerId( mResourceId );
		for ( DomainObject obj : collection ) {
			Entry entry = (Entry) obj;
			entry.setParentEntry( parent );
		}
		boolean result = UnitOfWork.getCurrent().commit( db );
		if ( result ) {
			updateSyncState( parent );
		}
	}

	@Override
	protected void updateSyncState( DomainObject obj )
	{
		try {
			Entry entry = (Entry) obj;
			entry.setSyncState( DomainObject.SyncState.SYNC_STATE_FULL );
		} catch ( ClassCastException e ) {
			e.printStackTrace();
		}
	}
}
