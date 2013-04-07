package com.symbyo.islamway.persistance.mappers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.symbyo.islamway.BuildConfig;
import com.symbyo.islamway.Utils;
import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.domain.DomainObject.SyncState;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.domain.Section;
import com.symbyo.islamway.persistance.Repository;
import org.eclipse.jdt.annotation.NonNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ScholarMapper extends AbstractMapper implements
		Section.ISectionFinder,
		Scholar.IScholarFinder {

	private enum ScholarField {
		ID( "_id" ),
		SERVER_ID( "server_id" ),
		NAME( "name" ),
		IMAGE_URL( "image_url" ),
		IMAGE_FILE( "image_local_path" ),
		QURAN_SYNC_STATE( "quran_sync_state" ),
		LESSONS_SYNC_STATE( "lessons_sync_state" );

		private final String mName;

		ScholarField( String name )
		{
			mName = name;
		}

		@Override
		public String toString()
		{
			return mName;
		}
	}

	private enum ScholarSectionField {
		ID( "_id" ),
		SECTION_ID( "section_id" ),
		SCHOLAR_ID( "scholar_id" );

		private final String mName;

		ScholarSectionField( String name )
		{
			mName = name;
		}

		@Override
		public String toString()
		{
			return mName;
		}
	}

	private enum SectionField {
		ID( "_id" ),
		TITLE( "title" ),
		SYNC_STATE( "sync_state" );

		private final String mValue;

		SectionField( String value )
		{
			mValue = value;
		}

		@Override
		public String toString()
		{
			return mValue;
		}
	}

	private static final String SCHOLAR_TABLE_NAME         = "scholar";
	private static final String SECTION_TABLE_NAME         = "section";
	private static final String SCHOLAR_SECTION_TABLE_NAME = "scholar_section";

	/**
	 * synthesize a SQL string with the shcolar fields.
	 *
	 * @param tableAliase appended to the field names if provided.
	 * @return
	 */
	private static String getScholarFields( String tableAliase )
	{
		if ( tableAliase == null ) {
			tableAliase = "";
		} else {
			tableAliase = tableAliase + ".";
		}
		StringBuilder bldr = new StringBuilder();
		ScholarField[] fields = ScholarField.values();
		for ( int i = 0, len = fields.length; i < len; i++ ) {
			bldr.append( tableAliase ).append( fields[i].toString() );
			if ( i == len - 1 ) {
				break;
			}
			bldr.append( "," );
		}
		return bldr.toString();
	}

	public ScholarMapper( @NonNull Context context )
	{
		super( context );
	}

	@Override
	protected Scholar doLoad( @NonNull Cursor c )
	{
		Scholar scholar;
		try {
			//_id
			int c_index = c.getColumnIndexOrThrow( ScholarField.ID.toString() );
			int id = c.getInt( c_index );

			//server_id
			c_index = c.getColumnIndexOrThrow(
					ScholarField.SERVER_ID.toString() );
			int server_id = c.getInt( c_index );

			//name
			c_index = c.getColumnIndexOrThrow( ScholarField.NAME.toString() );
			String name = c.isNull( c_index ) ? null : c.getString( c_index );

			//image_url
			c_index = c.getColumnIndexOrThrow(
					ScholarField.IMAGE_URL.toString() );
			String image_url = c.isNull( c_index ) ? null : c.getString(
					c_index );

			//image_local_path
			c_index = c.getColumnIndexOrThrow(
					ScholarField.IMAGE_FILE.toString() );
			String image_file = c.isNull( c_index ) ? null : c.getString(
					c_index );

			scholar = new Scholar( id, server_id, name, image_url, image_file );
		} catch ( IllegalArgumentException e ) {
			if ( BuildConfig.DEBUG ) {
				throw new Error( "column index does not exist." );
			}
		}
		return scholar;
	}

	@Override
	public void insert( @NonNull DomainObject obj, SQLiteDatabase db )
			throws SQLiteException
	{
		boolean is_in_transaction = false;
		if ( !(obj instanceof Scholar) ) {
			throw new Error( "DomainObject not instance of Scholar" );
		}
		Scholar scholar = (Scholar) obj;
		Utils.FormatedLog( "Scholar server_id: %d",
						   scholar.getServerId() );
		try {
			if ( db == null || !db.isOpen() ) {
				db = Repository.getInstance( mContext ).getWritableDatabase();
				db.beginTransaction();
				is_in_transaction = true;
			}
			ContentValues values = new ContentValues();
			values.put( ScholarField.SERVER_ID.toString(),
						scholar.getServerId() );
			values.put( ScholarField.NAME.toString(), scholar.getName() );
			values.put( ScholarField.IMAGE_URL.toString(),
						scholar.getImageUrl() );
			values.put( ScholarField.IMAGE_FILE.toString(), UUID.randomUUID()
					.toString() );
			// all newly inserted scholars are sync_basic.
			values.put( ScholarField.QURAN_SYNC_STATE.toString(),
						SyncState.SYNC_STATE_BASIC.ordinal() );
			values.put( ScholarField.LESSONS_SYNC_STATE.toString(),
						SyncState.SYNC_STATE_BASIC.ordinal() );

			long scholar_id =
					db.insertWithOnConflict( SCHOLAR_TABLE_NAME, null, values,
											 SQLiteDatabase.CONFLICT_REPLACE );

			Set<Section> sections = scholar.getSections();
			if ( sections.size() > 0 ) {
				values.clear();
				for ( Section section : sections ) {
					values.put( ScholarSectionField.SECTION_ID.toString(),
								section.getId() );
					values.put( ScholarSectionField.SCHOLAR_ID.toString(),
								scholar_id );
					db.insertWithOnConflict( SCHOLAR_SECTION_TABLE_NAME, null,
											 values,
											 SQLiteDatabase.CONFLICT_REPLACE );
				}
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
	public List<Scholar> findScholarsBySection( final Section section )
	{
		StatementSource stmt = new StatementSource() {

			@Override
			public String sql()
			{
				// FIXME this query returns all scholars in the table!!
				StringBuilder bldr;
				bldr = new StringBuilder( "SELECT " + getScholarFields( "sch" )
												  + " FROM " + SCHOLAR_TABLE_NAME + " AS sch" );
				bldr.append( " INNER JOIN " + SCHOLAR_SECTION_TABLE_NAME
									 + " AS sec" );
				bldr.append( " ON sch." + ScholarField.ID + " = sec."
									 + ScholarSectionField.SCHOLAR_ID );
				bldr.append(
						" WHERE " + ScholarSectionField.SECTION_ID + " = " )
						.append( section.getId() );
				bldr.append( " ORDER BY " ).append(
						ScholarField.NAME.toString() );
				return bldr.toString();
			}

			@Override
			public String[] parameters()
			{
				return null;
			}

		};

		List<Scholar> result = null;
		try {
			// unchecked but safe enough.
			result = (List<Scholar>) findMany( stmt );
		} catch ( ClassCastException e ) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public SyncState getQuranSyncState( Scholar scholar )
	{
		SyncState sync_state = null;
		SQLiteDatabase db;
		Cursor c = null;
		try {
			String[] columns = new String[]{ScholarField.QURAN_SYNC_STATE.toString()};
			String selection = ScholarField.ID + " = ?";
			String[] args = new String[]{Integer.toString( scholar.getId() )};
			db = Repository.getInstance( mContext ).getReadableDatabase();
			c = db.query( SCHOLAR_TABLE_NAME, columns, selection, args, null,
						  null, null );
			if ( c != null && c.getCount() == 1 ) {
				c.moveToFirst();
				sync_state = SyncState.values()[c.getInt( 0 )];
			}
		} catch ( SQLiteException e ) {
			e.printStackTrace();
		} finally {
			if ( c != null ) {
				c.close();
			}
		}
		return sync_state;
	}

	@Override
	public SyncState getLessonsSyncState( Scholar scholar )
	{
		SyncState sync_state = null;
		SQLiteDatabase db;
		Cursor c = null;
		try {
			String[] columns = new String[]{ScholarField.LESSONS_SYNC_STATE.toString()};
			String selection = ScholarField.ID + " = ?";
			String[] args = new String[]{Integer.toString( scholar.getId() )};
			db = Repository.getInstance( mContext ).getReadableDatabase();
			c = db.query( SCHOLAR_TABLE_NAME, columns, selection, args, null,
						  null, null );
			if ( c != null && c.getCount() == 1 ) {
				c.moveToFirst();
				sync_state = SyncState.values()[c.getInt( 0 )];
			}
		} catch ( SQLiteException e ) {
			e.printStackTrace();
		} finally {
			if ( c != null ) {
				c.close();
			}
		}
		return sync_state;
	}

	@Override
	public Scholar findScholarByServerId( final int server_id )
	{
		StatementSource stmt = new StatementSource() {
			@Override
			public String sql()
			{
				StringBuilder bldr;
				bldr = new StringBuilder( "SELECT * FROM " )
						.append( SCHOLAR_TABLE_NAME )
						.append( " WHERE " + ScholarField.SERVER_ID + " = ?" );
				return bldr.toString();
			}

			@Override
			public String[] parameters()
			{
				// TODO implement the method body
				return new String[]{Integer.toString( server_id )};
			}
		};

		Scholar scholar = null;
		try {
			scholar = (Scholar) findOne( stmt );
		} catch ( ClassCastException e ) {
			e.printStackTrace();
		}
		return scholar;
	}

	@Override
	public void updateScholarSyncState( Scholar scholar, SyncState state )
	{
		SQLiteDatabase db;
		try {
			ContentValues values = new ContentValues();
			values.put( ScholarField.QURAN_SYNC_STATE.toString(),
						Integer.toString( state.ordinal() ) );
			String whereClause = ScholarField.ID + " = ?";
			String[] whereArgs = new String[]{Integer.toString( scholar.getId() )};
			db = Repository.getInstance( mContext ).getReadableDatabase();
			db.update( SCHOLAR_TABLE_NAME, values, whereClause, whereArgs );
		} catch ( SQLiteException e ) {
			e.printStackTrace();
		}
	}

	@Override
	public SyncState getSectionSyncState( Section section )
	{
		SyncState sync_state = null;
		SQLiteDatabase db;
		Cursor c = null;
		try {
			String[] columns = new String[]{SectionField.SYNC_STATE
					.toString()};
			String selection = SectionField.ID + " = ?";
			String[] args = new String[]{Integer.toString( section.getId() )};
			db = Repository.getInstance( mContext ).getReadableDatabase();
			c = db.query( SECTION_TABLE_NAME, columns, selection, args, null,
						  null, null );
			if ( c != null && c.getCount() > 0 ) {
				c.moveToFirst();
				sync_state = SyncState.values()[c.getInt( 0 )];
			}
		} catch ( SQLiteException e ) {
			e.printStackTrace();
		} finally {
			if ( c != null ) {
				c.close();
			}
		}
		return sync_state;
	}

	public void updateSectionSyncState( Section section, SyncState state )
	{
		SQLiteDatabase db;
		try {
			ContentValues values = new ContentValues();
			values.put( SectionField.SYNC_STATE.toString(),
						Integer.toString( state.ordinal() ) );
			String whereClause = SectionField.ID + " = ?";
			String[] whereArgs = new String[]{Integer.toString( section
																		.getId() )};
			db = Repository.getInstance( mContext ).getReadableDatabase();
			db.update( SECTION_TABLE_NAME, values, whereClause, whereArgs );
		} catch ( SQLiteException e ) {
			e.printStackTrace();
		}
	}
}
