package com.symbyo.islamway.persistance;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.persistance.mappers.AbstractMapper;

public class UnitOfWork {

	private List<DomainObject>				newObjects	= new ArrayList<DomainObject>();

	private static ThreadLocal<UnitOfWork>	current		= new ThreadLocal<UnitOfWork>() {
															@Override
															protected
																	UnitOfWork
																	initialValue()
															{
																return new UnitOfWork();
															}
														};

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
	}

	public void commit( SQLiteDatabase db )
	{
		insertNew( db );
	}

	public void insertNew( SQLiteDatabase db )
	{
		try {
			db.beginTransaction();
			for ( DomainObject obj : newObjects ) {
				AbstractMapper mapper = Repository.getInstance().getMapper(
						obj.getClass() );
				mapper.insert( obj, db );
			}
			db.setTransactionSuccessful();
		} catch ( SQLiteException e ) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			newObjects.clear();
		}
	}
}
