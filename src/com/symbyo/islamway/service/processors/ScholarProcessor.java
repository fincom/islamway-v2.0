package com.symbyo.islamway.service.processors;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.domain.Section;
import com.symbyo.islamway.persistance.UnitOfWork;

public class ScholarProcessor extends Processor {

	private final Section	mSection;

	public ScholarProcessor(@NonNull Context context, Section section) {
		super( context );
		mSection = section;
	}

	@SuppressWarnings("null")
	@Override
	protected void doProcess( List<? extends DomainObject> collection,
			@NonNull SQLiteDatabase db )
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
		mPostProcessingListener.onPostProccessing( result );
	}
}
