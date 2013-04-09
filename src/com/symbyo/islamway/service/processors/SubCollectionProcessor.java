package com.symbyo.islamway.service.processors;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import com.symbyo.islamway.domain.DomainObject;

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
		// TODO implement the method body
	}

	@Override
	protected void updateSyncState( DomainObject obj )
	{
		// TODO implement the method body

	}
}
