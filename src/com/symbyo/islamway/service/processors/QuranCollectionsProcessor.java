package com.symbyo.islamway.service.processors;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import com.symbyo.islamway.IWApplication;
import com.symbyo.islamway.Utils;
import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.domain.Entry;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.persistance.Repository;
import com.symbyo.islamway.persistance.UnitOfWork;
import com.symbyo.islamway.persistance.mappers.ScholarMapper;
import com.symbyo.islamway.service.IWService;

import java.util.List;

/**
 * @author kdehairy
 * @since 2/24/13
 */
public class QuranCollectionsProcessor extends Processor {
	private final int mResourceId;

	public QuranCollectionsProcessor( Context context, int resource_id )
	{
		super( context );
		mResourceId = resource_id;
	}

	@Override
	protected void doProcess(
			List<? extends DomainObject> collection, SQLiteDatabase db,
			Intent pIntent )
	{
		Utils.FormatedLog( "processing %d collections", collection.size() );
		Utils.FormatedLog( "scholar id: %d", mResourceId );
		ScholarMapper mapper = (ScholarMapper) Repository.getInstance()
				.getMapper( Scholar.class );
		Scholar scholar = mapper.findScholarByServerId( mResourceId );
		for ( DomainObject obj : collection ) {
			Entry entry = (Entry) obj;
			entry.setScholar( scholar );
		}
		UnitOfWork.getCurrent().commit( db );

		/*int key = IWApplication.putDomainObjects( collection );
		pIntent.putExtra( IWService.EXTRA_DATA_KEY, key );*/
	}

}
