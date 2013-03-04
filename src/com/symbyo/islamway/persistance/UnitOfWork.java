package com.symbyo.islamway.persistance;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.symbyo.islamway.Utils;
import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.persistance.mappers.AbstractMapper;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

public class UnitOfWork {


	private final List<DomainObject> newObjects = new ArrayList<DomainObject>();

	// @formatter:off
	private static final ThreadLocal<UnitOfWork> current =
			new ThreadLocal<UnitOfWork>() {
				@Override
				protected UnitOfWork initialValue()
				{
					return new UnitOfWork();
				}
			};
	// @formatter:on

	public static UnitOfWork getCurrent()
	{
		return current.get();
	}

	public void registerNew( DomainObject object )
	{
		Assert.assertTrue( "object do exist in the database",
						   object.getId() == DomainObject.INVALID_ID );
		if ( newObjects.contains( object ) ) {
			return;
		}
		newObjects.add( object );
		Utils.FormatedLog( "New Object registered: %s",
								  object.toString() );
	}

	/**
	 * @param db
	 * @return true if successful, false otherwise
	 */
	public boolean commit( SQLiteDatabase db )
	{
		return insertNew( db );
	}

	public boolean insertNew( SQLiteDatabase db )
	{
		boolean result = true;
		try {
			Utils.FormatedLog( "inserting %d objects",
								   newObjects.size() );
			db.beginTransaction();
			for ( DomainObject obj : newObjects ) {
				AbstractMapper mapper = Repository.getInstance().getMapper(
						obj.getClass() );
				mapper.insert( obj, db );
			}
			db.setTransactionSuccessful();
		} catch ( SQLiteException e ) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
			newObjects.clear();
		}
		return result;
	}
}
