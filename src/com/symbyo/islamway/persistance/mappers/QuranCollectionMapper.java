package com.symbyo.islamway.persistance.mappers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.symbyo.islamway.BuildConfig;
import com.symbyo.islamway.domain.Collection;
import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.domain.Entry;
import com.symbyo.islamway.domain.Scholar;
import org.eclipse.jdt.annotation.NonNull;


import java.util.List;

/**
 * @author kdehairy
 * @since 3/27/13
 */
public class QuranCollectionMapper extends AbstractMapper implements
		Entry.IQuranCollectionFinder {
	public QuranCollectionMapper( Context context )
	{
		super( context );
	}

	@Override
	public void insert( @NonNull DomainObject obj, SQLiteDatabase db )
			throws SQLiteException
	{
		// TODO implement the method body

	}

	@Override
	protected DomainObject doLoad( @NonNull Cursor c )
	{
		Collection collection;
		try {
			// TODO return a Collection object from cursor.
		} catch ( IllegalArgumentException e ) {
			if ( BuildConfig.DEBUG ) {
				throw new Error ( "Column index does not exist" );
			}
		}
		return null;
	}

	@Override
	public List<Collection> getScholarQuranEntries( Scholar scholar )
	{
		// TODO implement the method body
		return null;
	}
}
