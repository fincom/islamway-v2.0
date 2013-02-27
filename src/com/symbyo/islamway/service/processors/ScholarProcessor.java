package com.symbyo.islamway.service.processors;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.domain.Section;
import com.symbyo.islamway.persistance.Repository;
import com.symbyo.islamway.persistance.UnitOfWork;
import com.symbyo.islamway.persistance.mappers.ScholarMapper;
import org.eclipse.jdt.annotation.NonNull;

import java.util.List;

public class ScholarProcessor extends Processor {

	private final Section mSection;

	public ScholarProcessor( @NonNull Context context, Section section )
	{
		super( context );
		mSection = section;
	}

	@SuppressWarnings("null")
	@Override
	protected void doProcess( List<? extends DomainObject> collection,
							  @NonNull SQLiteDatabase db, Intent pIntent )
	{
		if ( mSection != null ) {
			Log.d( "IWService",
				   String.format( "processing %d scholars",
								  collection.size() ) );
			for ( DomainObject obj : collection ) {
				Scholar scholar = (Scholar) obj;
				scholar.addSection( mSection );
			}
		}
		boolean result = UnitOfWork.getCurrent().commit( db );
		if ( result ) {
			ScholarMapper mapper = (ScholarMapper) Repository
					.getInstance( mContext )
					.getMapper(
							Scholar.class );
			mapper.updateSectionSyncState( mSection,
										   DomainObject.SyncState.SYNC_STATE_FULL );
		}
	}
}
