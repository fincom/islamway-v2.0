package com.symbyo.islamway.persistance.mappers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.symbyo.islamway.BuildConfig;
import com.symbyo.islamway.Utils;
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
public class EntryMapper extends AbstractMapper implements
		Entry.EntryFinder {


	@Override
	public List<Entry> getScholarEntriesByTypes( final Scholar scholar,
												 final Entry.EntryType[] types )
	{
		StatementSource stmt = new StatementSource() {
			@Override
			public String sql()
			{
				StringBuilder bldr = new StringBuilder(
						"SELECT " + getEntryFields( "c" ) );
				bldr.append( " FROM " + ENTRY_TABLE_NAME + " AS c" )
						.append( " INNER JOIN "
										 + ScholarMapper.SCHOLAR_TABLE_NAME
										 + " AS s" )
						.append( " ON s." + ScholarMapper.ScholarField.ID )
						.append( " = c." + EntryField.SCHOLAR_ID )
						.append( " WHERE s." + ScholarMapper.ScholarField.ID )
						.append( " = ?" )
						.append( " AND " + EntryField.PARENT_COLLECTION_ID )
						.append( " = " + Entry.INVALID_ID )
						.append( " AND " + EntryField.TYPE + " IN (" );
				for ( int i = 0, len = types.length; i < len; i++ ) {
					bldr.append( " ?," );
				}
				String sql = bldr.toString();
				sql = sql.substring( 0, sql.length() - 1 );
				sql = sql + ")";
				Utils.Log( sql );
				return sql;
			}

			@Override
			public String[] parameters()
			{
				String[] str = new String[types.length + 1];
				str[0] = Integer.toString( scholar.getId() );
				for ( int i = 0, len = types.length; i < len; i++ ) {
					str[i + 1] = types[i].toString();
				}
				return str;
			}
		};
		List<Entry> result = null;
		try {
			// unchecked but safe enough.
			result = (List<Entry>) findMany( stmt );
		} catch ( ClassCastException e ) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public Entry findEntryByServerId( final int server_id )
	{
		StatementSource stmt = new StatementSource() {
			@Override
			public String sql()
			{
				StringBuilder bldr;
				bldr = new StringBuilder( "SELECT * FROM " )
						.append( ENTRY_TABLE_NAME )
						.append( " WHERE " + EntryField.SERVER_ID + " = ?" );
				return bldr.toString();
			}

			@Override
			public String[] parameters()
			{
				return new String[]{Integer.toString( server_id )};
			}
		};

		Entry entry = null;
		try {
			entry = (Entry) findOne( stmt );
		} catch ( ClassCastException e ) {
			e.printStackTrace();
		}
		return entry;
	}

	@Override
	public void updateSyncState( Entry entry,
								 DomainObject.SyncState sync_state )
	{
		SQLiteDatabase db;
		try {
			ContentValues values = new ContentValues();
			values.put( EntryField.SYNC_STATE.toString(),
						Integer.toString( sync_state.ordinal() ) );
			String whereClause = EntryField.ID + " = ?";
			String[] whereArgs =
					new String[]{Integer.toString( entry.getId() )};
			db = Repository.getInstance( mContext ).getReadableDatabase();
			db.update( ENTRY_TABLE_NAME, values, whereClause, whereArgs );
		} catch ( SQLiteException e ) {
			e.printStackTrace();
		}
	}

	public enum EntryField {
		ID( "_id" ),
		SERVER_ID( "server_id" ),
		TITLE( "title" ),
		ENTRIES_COUNT( "entries_count" ),
		SCHOLAR_ID( "scholar_id" ),
		PARENT_COLLECTION_ID( "parent_collection_id" ),
		PUBLISHED_AT( "published_at" ),
		VIEW_ORDER( "view_order" ),
		NARRATION( "narration" ),
		TYPE( "type" ),
		SYNC_STATE( "sync_state" );

		private final String mValue;

		EntryField( String value )
		{
			mValue = value;
		}

		@Override
		public String toString()
		{
			return mValue;
		}
	}

	public static final String ENTRY_TABLE_NAME = "entry";

	private static String getEntryFields( String tableAliase )
	{
		if ( tableAliase == null ) {
			tableAliase = "";
		} else {
			tableAliase = tableAliase + ".";
		}
		StringBuilder bldr = new StringBuilder();
		EntryField[] fields = EntryField.values();
		for ( int i = 0, len = fields.length; i < len; i++ ) {
			bldr.append( tableAliase ).append( fields[i].toString() );
			if ( i == len - 1 ) {
				break;
			}
			bldr.append( "," );
		}
		return bldr.toString();
	}

	public EntryMapper( Context context )
	{
		super( context );
	}

	@Override
	public void insert( @NonNull DomainObject obj, SQLiteDatabase db )
			throws SQLiteException
	{
		boolean is_in_transaction = false;
		if ( !(obj instanceof Entry) ) {
			throw new Error( "DomainObject not instance of Entry" );
		}
		Entry entry = (Entry) obj;
		Utils.FormatedLog( "Entry server_id: %d", entry.getServerId() );
		try {
			if ( db == null || !db.isOpen() ) {
				db = Repository.getInstance( mContext ).getWritableDatabase();
				db.beginTransaction();
				is_in_transaction = true;
			}
			ContentValues values = new ContentValues();
			values.put( EntryField.SERVER_ID.toString(),
						entry.getServerId() );
			values.put( EntryField.TITLE.toString(),
						entry.getTitle() );
			values.put( EntryField.ENTRIES_COUNT.toString(),
						entry.getEntriesCount() );
			values.put( EntryField.SCHOLAR_ID.toString(),
						entry.getScholarId() );
			values.put( EntryField.PARENT_COLLECTION_ID.toString(),
						entry.getParentEntryId() );
			values.put( EntryField.PUBLISHED_AT.toString(),
						entry.getPublishedAt() );
			values.put( EntryField.VIEW_ORDER.toString(),
						entry.getViewOrder() );
			values.put( EntryField.NARRATION.toString(),
						entry.getNarration() );
			values.put( EntryField.TYPE.toString(),
						entry.getType().toString() );
			values.put( EntryField.SYNC_STATE.toString(),
						DomainObject.SyncState.SYNC_STATE_NONE.ordinal() );

			db.insertWithOnConflict( ENTRY_TABLE_NAME, null, values,
									 SQLiteDatabase.CONFLICT_REPLACE );
			Utils.Log( "Entry saved" );

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
		Entry entry = null;
		try {
			//_id
			int c_index = c.getColumnIndexOrThrow(
					EntryField.ID.toString() );
			int id = c.getInt( c_index );

			//server_id
			c_index = c.getColumnIndexOrThrow(
					EntryField.SERVER_ID.toString() );
			int server_id = c.getInt( c_index );

			//title
			c_index = c.getColumnIndexOrThrow(
					EntryField.TITLE.toString() );
			String title = c.isNull( c_index ) ? null : c.getString( c_index );

			//entries_count
			c_index = c.getColumnIndexOrThrow(
					EntryField.ENTRIES_COUNT.toString() );
			int entries_count = c.isNull( c_index ) ? 0 : c.getInt( c_index );

			//scholar_id
			c_index = c.getColumnIndexOrThrow(
					EntryField.SCHOLAR_ID.toString() );
			int scholar_id = c.isNull( c_index ) ? Entry.INVALID_ID : c
					.getInt( c_index );

			//parent_collection_id
			c_index = c.getColumnIndexOrThrow(
					EntryField.PARENT_COLLECTION_ID.toString() );
			int parent_entry_id = c.isNull( c_index ) ? Entry.INVALID_ID : c
					.getInt( c_index );

			//published_at
			c_index = c.getColumnIndexOrThrow(
					EntryField.PUBLISHED_AT.toString() );
			String published_at =
					c.isNull( c_index ) ? null : c.getString( c_index );

			//view_order
			c_index = c.getColumnIndexOrThrow(
					EntryField.VIEW_ORDER.toString() );
			int view_order = c.isNull( c_index ) ? 0 : c.getInt( c_index );

			//narration
			c_index = c.getColumnIndexOrThrow(
					EntryField.NARRATION.toString() );
			String narration =
					c.isNull( c_index ) ? null : c.getString( c_index );

			//type
			c_index = c.getColumnIndexOrThrow( EntryField.TYPE.toString() );
			Entry.EntryType type =
					Entry.EntryType.values()[c.getInt( c_index )];

			// create an Entry object
			entry = new Entry( id, server_id, title, type );
			entry.setEntriesCount( entries_count );
			entry.setScholarId( scholar_id );
			entry.setParentEntryId( parent_entry_id );
			entry.setPublishedAt( published_at );
			entry.setViewOrder( view_order );
			entry.setNarration( narration );

		} catch ( IllegalArgumentException e ) {
			if ( BuildConfig.DEBUG ) {
				throw new Error( "Column index does not exist" );
			}
		}
		return entry;
	}
}
