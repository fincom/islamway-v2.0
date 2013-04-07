package com.symbyo.islamway.persistance.mappers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.symbyo.islamway.Utils;
import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.persistance.Repository;
import org.eclipse.jdt.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * All mapper classes extending this class should provide a public enum called
 * Field that override the toString method to return the field name
 * corresponding to each enum value.
 *
 * @author kdehairy
 */
public abstract class AbstractMapper {

	protected final Context mContext;

	public AbstractMapper( @NonNull Context context )
	{
		mContext = context;
	}

	/**
	 * Finds multiple DomainObjects of the same type based on a parameterized
	 * Sql statement.
	 *
	 * @param stmt
	 * @return
	 */
	protected List<? extends DomainObject> findMany( StatementSource stmt )
	{
		List<DomainObject> objects = null;
		SQLiteDatabase db;
		Cursor c = null;
		try {
			Utils.Log( "opening the database for read." );
			db = Repository.getInstance( mContext ).getReadableDatabase();
			Utils.Log( stmt.sql() );
			c = db.rawQuery( stmt.sql(), stmt.parameters() );
			if ( c != null ) {
				objects = loadAll( c );
			}
		} catch ( SQLiteException e ) {
			e.printStackTrace();
		} finally {
			if ( c != null ) {
				c.close();
			}
		}

        /*List<DomainObject> result = null;
		if ( objects != null ) {
			result = Collections.unmodifiableList( objects );
		}*/
		return objects;
	}

	/**
	 * Finds multiple DomainObjects of the same type based on a parameterized
	 * Sql statement.
	 *
	 * @param stmt
	 * @return
	 */
	protected DomainObject findOne( StatementSource stmt ) {
		DomainObject obj = null;
		SQLiteDatabase db;
		Cursor c = null;
		try {
			Utils.Log( "opening the database for read." );
			db = Repository.getInstance( mContext ).getReadableDatabase();
			Utils.Log( stmt.sql() );
			c = db.rawQuery( stmt.sql(), stmt.parameters() );
			if ( c != null ) {
				c.moveToFirst();
				obj = load( c );
			}
		} catch ( SQLiteException e ) {
			e.printStackTrace();
		} finally {
			if ( c != null ) {
				c.close();
			}
		}
		return obj;
	}

	/**
	 * Loads a multiple row Cursor into a List of DomainObjects.
	 *
	 * @param c
	 * @return
	 */
	private List<DomainObject> loadAll( @NonNull Cursor c )
	{
		ArrayList<DomainObject> objects = null;
		int count;
		if ( (count = c.getCount()) > 0 ) {
			objects = new ArrayList<DomainObject>( count );
			while ( c.moveToNext() ) {
				DomainObject obj = load( c );
				objects.add( obj );
			}
		}
		return objects;
	}

	/**
	 * Loads a single row Cursor into a DomainObject.
	 *
	 * @param c
	 * @return
	 */
	private DomainObject load( @NonNull Cursor c )
	{
		return doLoad( c );
	}

	public void insert( @NonNull DomainObject obj ) throws SQLiteException
	{
		insert( obj, null );
	}

	public abstract void insert( @NonNull DomainObject obj, SQLiteDatabase db )
			throws SQLiteException;

	protected abstract DomainObject doLoad( @NonNull Cursor c );
}
