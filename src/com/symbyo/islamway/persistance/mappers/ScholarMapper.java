package com.symbyo.islamway.persistance.mappers;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.jdt.annotation.NonNull;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.domain.DomainObject.SyncState;
import com.symbyo.islamway.domain.IScholarFinder;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.domain.Section;
import com.symbyo.islamway.persistance.Repository;

public class ScholarMapper extends AbstractMapper implements IScholarFinder {

	public enum Material {
		QURAN,
		LESSON
	}

	private enum ScholarField {
		ID("_id"),
		SERVER_ID("server_id"),
		NAME("name"),
		EMAIL("email"),
		PHONE("phone"),
		PAGE_URL("page_url"),
		IMAGE_URL("image_url"),
		IMAGE_FILE("image_file"),
		VIEW_COUNT("view_count"),
		POPULARITY("popularity");

		private final String	mName;

		ScholarField(String name) {
			mName = name;
		}

		@Override
		public String toString()
		{
			return mName;
		}
	}

	private enum ScholarSectionField {
		ID("_id"),
		SECTION_ID("section_id"),
		SCHOLAR_ID("scholar_id");

		private final String	mName;

		ScholarSectionField(String name) {
			mName = name;
		}

		@Override
		public String toString()
		{
			return mName;
		}
	}

	private enum SectionField {
		ID("_id"),
		TITLE("title"),
		SYNC_STATE("sync_state");

		private final String	mValue;

		SectionField(String value) {
			mValue = value;
		}

		@Override
		public String toString()
		{
			return mValue;
		}
	}

	private static final String	SCHOLAR_TABLE_NAME			= "scholar";
	private static final String	SECTION_TABLE_NAME			= "section";
	private static final String	SCHOLAR_SECTION_TABLE_NAME	= "scholar_section";

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

	public ScholarMapper(@NonNull Context context) {
		super( context );
	}

	/*
	 * @Override protected String findAllStatement(ContentValues params) {
	 * StringBuilder bldr; bldr = new StringBuilder("SELECT " + getFields() +
	 * "FROM " + TABLE_NAME); bldr.append(" WHERE"); if (params != null) {
	 * Set<Entry<String, Object>> entries = params.valueSet(); int size =
	 * entries.size(); int i = 1; for (Entry<String, Object> entry : entries) {
	 * String key = entry.getKey(); String value = (String) entry.getValue();
	 * bldr.append(" " + key + " = '" + value + "'"); if (i != size) {
	 * bldr.append(" AND "); } i++; } } bldr.append(" ORDER BY " +
	 * Field.NAME.toString());
	 * 
	 * return bldr.toString(); }
	 */

	static class FindByFieldValue implements StatementSource {
        private final ScholarField mField;
        private final String       mValue;

        public FindByFieldValue(
                @NonNull ScholarField field,
                @NonNull String value )
        {
            mField = field;
            mValue = value;
        }

        @Override
        public String sql()
        {
            StringBuilder bldr;
            bldr = new StringBuilder( "SELECT " + getScholarFields( null )
                    + "FROM " + SCHOLAR_TABLE_NAME );
            bldr.append( " WHERE " ).append( mField.toString() ).append(
                    " = ?" );
            bldr.append( " ORDER BY " ).append( ScholarField.NAME.toString() );

            return bldr.toString();
        }

        @Override
        public String[] parameters()
        {
            return new String[]{mValue};
        }

    }

	/*
	 * static class FindQuranScholars implements StatementSource {
	 * 
	 * @Override public String sql() { StringBuilder bldr; bldr = new
	 * StringBuilder("SELECT " + getScholarFields(false) + "FROM " +
	 * SCHOLAR_TABLE_NAME); bldr.append(" ORDER BY " +
	 * ScholarField.NAME.toString()); return bldr.toString(); }
	 * 
	 * @Override public String[] parameters() { return null; }
	 * 
	 * }
	 */

    @Override
    protected Scholar doLoad( @NonNull Cursor c )
    {
        Scholar scholar;
        try {
            int id = c.getInt( c.getColumnIndexOrThrow( ScholarField.ID
                                                                    .toString() ) );
            int server_id = c
                    .getInt( c.getColumnIndexOrThrow( ScholarField.SERVER_ID
                                                                  .toString() ) );
            String name = c.isNull( c.getColumnIndexOrThrow( ScholarField.NAME
                                                                         .toString() ) ) ? null : c
                    .getString( c
                            .getColumnIndexOrThrow(
                                    ScholarField.NAME.toString() ) );
            String email = c.isNull( c
                    .getColumnIndexOrThrow( ScholarField.EMAIL.toString() ) )
                    ? null
                    : c.getString( c.getColumnIndexOrThrow( ScholarField.EMAIL
                                                                        .toString() ) );
            String phone = c.isNull( c
                    .getColumnIndexOrThrow( ScholarField.PHONE.toString() ) )
                    ? null
                    : c.getString( c.getColumnIndexOrThrow( ScholarField.PHONE
                                                                        .toString() ) );
            String page_url = c.isNull( c
                    .getColumnIndexOrThrow( ScholarField.PAGE_URL.toString() ) )
                    ? null
                    : c.getString( c
                    .getColumnIndexOrThrow( ScholarField.PAGE_URL
                                                        .toString() ) );
            String image_url = c
                    .isNull( c.getColumnIndexOrThrow( ScholarField.IMAGE_URL
                                                                  .toString() ) ) ? null : c
                    .getString( c.getColumnIndexOrThrow( ScholarField.IMAGE_URL
                                                                     .toString() ) );
            String image_file = c
                    .isNull( c.getColumnIndexOrThrow( ScholarField.IMAGE_FILE
                                                                  .toString() ) ) ? null : c
                    .getString( c
                            .getColumnIndexOrThrow( ScholarField.IMAGE_FILE
                                                                .toString() ) );
            int view_count = c
                    .getInt( c.getColumnIndexOrThrow( ScholarField.VIEW_COUNT
                                                                  .toString() ) );
            int popularity = c
                    .getInt( c.getColumnIndexOrThrow( ScholarField.POPULARITY
                                                                  .toString() ) );
            scholar = new Scholar( id, server_id, name, email, phone, page_url,
                    image_url, image_file, view_count, popularity );
        } catch ( IllegalArgumentException e ) {
            // TODO: remove error checking after testing
            throw new Error( "column index does not exist." );
        }
        return scholar;
    }

    @Override
    public Scholar findByPk( int id )
    {
        // TODO Auto-generated method stub
        return null;
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
		Log.d( "ScholarMapper",
				String.format( "Scholar server_id: %d", scholar.getServerId() ) );
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
			values.put( ScholarField.EMAIL.toString(), scholar.getEmail() );
			values.put( ScholarField.PHONE.toString(), scholar.getPhone() );
			values.put( ScholarField.PAGE_URL.toString(), scholar.getPageUrl() );
			values.put( ScholarField.IMAGE_URL.toString(),
					scholar.getImageUrl() );
			values.put( ScholarField.IMAGE_FILE.toString(), UUID.randomUUID()
					.toString() );
			values.put( ScholarField.VIEW_COUNT.toString(),
					scholar.getViewCount() );
			values.put( ScholarField.POPULARITY.toString(),
					scholar.getPopularity() );

			long scholar_id = db.insertWithOnConflict( SCHOLAR_TABLE_NAME,
					null, values, SQLiteDatabase.CONFLICT_REPLACE );

			Set<Section> sections = scholar.getSections();
			if ( sections.size() > 0 ) {
				values.clear();
				for ( Section section : sections ) {
					values.put( ScholarSectionField.SECTION_ID.toString(),
							section.getId() );
					values.put( ScholarSectionField.SCHOLAR_ID.toString(),
							scholar_id );
					db.insertWithOnConflict( SCHOLAR_SECTION_TABLE_NAME, null,
							values, SQLiteDatabase.CONFLICT_REPLACE );
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
	public SyncState getSectionSyncState( Section section )
	{
		SyncState sync_state = null;
		SQLiteDatabase db;
		Cursor c = null;
		try {
			String[] columns = new String[] { SectionField.SYNC_STATE
					.toString() };
			String selection = SectionField.ID + " = ?";
			String[] args = new String[] { Integer.toString( section.getId() ) };
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
			String[] whereArgs = new String[] { Integer.toString( section
					.getId() ) };
			db = Repository.getInstance( mContext ).getReadableDatabase();
			db.update( SECTION_TABLE_NAME, values, whereClause, whereArgs );
		} catch ( SQLiteException e ) {
			e.printStackTrace();
		}
	}
}
