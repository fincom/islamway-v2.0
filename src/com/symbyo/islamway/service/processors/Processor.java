package com.symbyo.islamway.service.processors;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.persistance.Repository;

public abstract class Processor {

	protected Context					mContext;
	protected OnPostProccessingListener	mPostProcessingListener;

	public Processor(@NonNull Context context) {
		mContext = context;
	}

	public void
			process( @NonNull List<? extends DomainObject> domain_collection )
					throws ProcessingException
	{
		try {
			SQLiteDatabase db = Repository.getInstance( mContext )
					.getWritableDatabase();
			if ( db == null ) {
				throw new SQLiteException();
			}
			Log.d( "IWService",
					String.format( "processing %d objects",
							domain_collection.size() ) );
			doProcess( domain_collection, db );
		} catch ( SQLiteException e ) {
			try {
				wait( 1000 );
				SQLiteDatabase db = Repository.getInstance( mContext )
						.getWritableDatabase();
				if ( db == null ) {
					throw new ProcessingException();
				}
				doProcess( domain_collection, db );
			} catch ( SQLiteException ex ) {
				throw new ProcessingException();
			} catch ( InterruptedException e1 ) {
				throw new ProcessingException();
			}
		}

	}

	public void
			setOnPostProcessingListener( OnPostProccessingListener listener )
	{
		mPostProcessingListener = listener;
	}

	protected abstract void doProcess( List<? extends DomainObject> collection,
			@NonNull SQLiteDatabase db );
}
