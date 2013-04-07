package com.symbyo.islamway.persistance.mappers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.symbyo.islamway.BuildConfig;
import com.symbyo.islamway.Utils;
import com.symbyo.islamway.domain.Collection;
import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.domain.Entry;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.persistance.Repository;
import org.eclipse.jdt.annotation.NonNull;

import java.util.List;

/**
 * @author kdehairy
 * @since 3/27/13
 */
public class CollectionMapper extends AbstractMapper implements
		Entry.ICollectionFinder {

	private enum QuranCollectionField {
		ID( "_id" ),
		SERVER_ID( "server_id" ),
		TITLE( "title" ),
		ENTRIES_COUNT( "entries_count" ),
		SCHOLAR_ID( "scholar_id" ),
		SYNC_STATE( "sync_state" ),
		TYPE( "type" );

		private final String mValue;

		QuranCollectionField( String value )
		{
			mValue = value;
		}

		@Override
		public String toString()
		{
			return mValue;
		}
	}

	private static final String QURAN_COLLECTION_TABLE_NAME =
			"quran_collection";

	private static String getQuranCollectionFields( String tableAliase )
	{
		if ( tableAliase == null ) {
			tableAliase = "";
		} else {
			tableAliase = tableAliase + ".";
		}
		StringBuilder bldr = new StringBuilder();
		QuranCollectionField[] fields = QuranCollectionField.values();
		for ( int i = 0, len = fields.length; i < len; i++ ) {
			bldr.append( tableAliase ).append( fields[i].toString() );
			if ( i == len - 1 ) {
				break;
			}
			bldr.append( "," );
		}
		return bldr.toString();
	}

	public CollectionMapper( Context context )
	{
		super( context );
	}

	@Override
	public void insert( @NonNull DomainObject obj, SQLiteDatabase db )
			throws SQLiteException
	{
		boolean is_in_transaction = false;
		if ( !(obj instanceof Collection) ) {
			throw new Error( "DomainObject not instance of Collection" );
		}
		Collection collection = (Collection) obj;
		Utils.FormatedLog( "Collection server_id: %d",
						   collection.getServerId() );
		try {
			if ( db == null || !db.isOpen() ) {
				db = Repository.getInstance( mContext ).getWritableDatabase();
				db.beginTransaction();
				is_in_transaction = true;
			}
			ContentValues values = new ContentValues();
			values.put( QuranCollectionField.SERVER_ID.toString(),
						collection.getServerId() );
			values.put( QuranCollectionField.TITLE.toString(),
						collection.getTitle() );
			values.put( QuranCollectionField.ENTRIES_COUNT.toString(),
						collection.getEntriesCount() );
			values.put( QuranCollectionField.SCHOLAR_ID.toString(),
						collection.getScholar().getId() );
			values.put( QuranCollectionField.SYNC_STATE.toString(),
						DomainObject.SyncState.SYNC_STATE_NONE.ordinal() );
			values.put( QuranCollectionField.TYPE.toString(),
						collection.getType().toString() );
			Utils.FormatedLog( "server_id: %d", collection.getServerId() );
			Utils.FormatedLog( "scholar_id: %d", collection.getScholar().getId() );
			switch ( collection.getType() ) {
				case MUSHAF:
					db.insertWithOnConflict( QURAN_COLLECTION_TABLE_NAME, null,
											 values,
											 SQLiteDatabase.CONFLICT_REPLACE );
					Utils.FormatedLog( "Collection saved as %s",
									   collection.getType().toString() );
			}
			if ( is_in_transaction ) {
				db.setTransactionSuccessful();
			}
		} finally {
			if ( is_in_transaction ) {
				db.endTransaction();
			}
		}
	}

	@Override
	protected DomainObject doLoad( @NonNull Cursor c )
	{
		Collection collection;
		try {
			// TODO return a Collection object from cursor.
		} catch ( IllegalArgumentException e ) {
			if ( BuildConfig.DEBUG ) {
				throw new Error( "Column index does not exist" );
			}
		}
		return null;
	}

	@Override
	public List<Collection> getScholarQuranCollections( Scholar scholar )
	{
		StatementSource stmt = new StatementSource() {
			@Override
			public String sql()
			{
				// TODO implement the method body
//				StringBuilder bldr =
//						new StringBuilder( "SELECT " + getQuranCollectionFields(
//								"c" ) + " FROM " + QURAN_COLLECTION_TABLE_NAME + " AS c" );
				return null;
			}

			@Override
			public String[] parameters()
			{
				// TODO implement the method body
				return new String[0];
			}
		};
		return null;
	}

	@Override
	public List<Entry> getEntries( Collection collection )
	{
		// TODO implement the method body
		return null;
	}
}
