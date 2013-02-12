package com.symbyo.islamway.persistance;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.persistance.mappers.AbstractMapper;

public class UnitOfWork {


	private List<DomainObject>				newObjects	= new ArrayList<DomainObject>();

	// @formatter:off
	private static ThreadLocal<UnitOfWork>	current		= new ThreadLocal<UnitOfWork>() {
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
		Log.d( "Islamway", String.format( "New Object registered: %s", object.toString() ) );
	}

	/**
	 * 
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
			Log.d( "Islamway",
					String.format( "inserting %d objects", newObjects.size() ) );
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
