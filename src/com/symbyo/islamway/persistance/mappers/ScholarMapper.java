package com.symbyo.islamway.persistance.mappers;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.domain.IScholarFinder;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.persistance.Repository;
import com.symbyo.islamway.service.IWService.Section;

public class ScholarMapper extends AbstractMapper implements IScholarFinder{
	
	public enum Material {
		QURAN,
		LESSON
	}
	
	private enum ScholarField {
		ID ("_id"),
		SERVER_ID ("server_id"),
		NAME ("name"),
		EMAIL ("email"),
		PHONE ("phone"),
		PAGE_URL ("page_url"),
		IMAGE_URL ("image_url"),
		IMAGE_FILE ("image_file"),
		VIEW_COUNT ("view_count"),
		POPULARITY ("popularity");
		
		private final String mName;
		
		ScholarField(String name) {
			mName = name;
		}
		
		@Override
		public String toString() {
			return mName;
		}
	}
	
	private enum ScholarSectionsField {
		ID ("_id"),
		SECTION ("section"),
		SCHOLAR_ID ("scholar_id");
		
		private final String mName;
		
		ScholarSectionsField(String name) {
			mName = name;
		}
		
		@Override
		public String toString() {
			return mName;
		}
	}
	
	private static final String SCHOLAR_TABLE_NAME = "scholar";
	private static final String SECTION_TABLE_NAME = "scholar_sections";
	

	private static String getScholarFields(String tableAliase) {
		if (tableAliase == null) {
			tableAliase = "";
		} else {
			tableAliase = tableAliase + ".";
		}
		StringBuilder bldr = new StringBuilder();
		ScholarField[] fields = ScholarField.values();
		for (int i = 0, len = fields.length; i< len; i++) {
			bldr.append(tableAliase + fields[i].toString());
			if (i == len - 1) {
				break;
			}
			bldr.append(",");
		}
		return bldr.toString();
	}

	public ScholarMapper(@NonNull Context context) {
		super(context);
	}

	/*@Override
	protected String findAllStatement(ContentValues params) {
		StringBuilder bldr;
		bldr = new StringBuilder("SELECT " + getFields() + "FROM " + TABLE_NAME);
		bldr.append(" WHERE");
		if (params != null) {
			Set<Entry<String, Object>> entries = params.valueSet();
			int size = entries.size();
			int i = 1;
			for (Entry<String, Object> entry : entries) {
				String key = entry.getKey();
				String value = (String) entry.getValue();
				bldr.append(" " + key + " = '" + value + "'");
				if (i != size) {
					bldr.append(" AND ");
				}
				i++;
			}
		}
		bldr.append(" ORDER BY " + Field.NAME.toString());
		
		return bldr.toString();
	}*/
	
	static class FindByFieldValue implements StatementSource {
		 private ScholarField mField;
		 private String mValue;
		 
		 public FindByFieldValue(@NonNull ScholarField field, @NonNull String value) {
			 mField = field;
			 mValue = value;
		 }

		@Override
		public String sql() {
			StringBuilder bldr;
			bldr = new StringBuilder("SELECT " + getScholarFields(null) + "FROM " + SCHOLAR_TABLE_NAME);
			bldr.append(" WHERE " + mField.toString() + " = ?");
			bldr.append(" ORDER BY " + ScholarField.NAME.toString());
			
			return bldr.toString();
		}

		@Override
		public String[] parameters() {
			return new String[] {mValue};
		}
		
	}
	
	/*static class FindQuranScholars implements StatementSource {

		@Override
		public String sql() {
			StringBuilder bldr;
			bldr = new StringBuilder("SELECT " + getScholarFields(false) + "FROM " + SCHOLAR_TABLE_NAME);
			bldr.append(" ORDER BY " + ScholarField.NAME.toString());
			return bldr.toString();
		}

		@Override
		public String[] parameters() {
			return null;
		}
		
	}*/

	@Override
	protected Scholar doLoad(@NonNull Cursor c) {
		Scholar scholar = null;
		try {
			int id = c.getInt(c.getColumnIndexOrThrow(ScholarField.ID.toString()));
			int server_id = c.getInt(c.getColumnIndexOrThrow(ScholarField.SERVER_ID.toString()));
			String name = c.isNull(c.getColumnIndexOrThrow(ScholarField.NAME.toString())) ?
					null : c.getString(c.getColumnIndexOrThrow(ScholarField.NAME.toString()));
			String email = c.isNull(c.getColumnIndexOrThrow(ScholarField.EMAIL.toString())) ?
					null : c.getString(c.getColumnIndexOrThrow(ScholarField.EMAIL.toString()));
			String phone = c.isNull(c.getColumnIndexOrThrow(ScholarField.PHONE.toString())) ?
					null : c.getString(c.getColumnIndexOrThrow(ScholarField.PHONE.toString()));
			String page_url = c.isNull(c.getColumnIndexOrThrow(ScholarField.PAGE_URL.toString())) ?
					null : c.getString(c.getColumnIndexOrThrow(ScholarField.PAGE_URL.toString()));
			String image_url = c.isNull(c.getColumnIndexOrThrow(ScholarField.IMAGE_URL.toString())) ?
					null : c.getString(c.getColumnIndexOrThrow(ScholarField.IMAGE_URL.toString()));
			String image_file = c.isNull(c.getColumnIndexOrThrow(ScholarField.IMAGE_FILE.toString())) ?
					null : c.getString(c.getColumnIndexOrThrow(ScholarField.IMAGE_FILE.toString()));
			int view_count = c.getInt(c.getColumnIndexOrThrow(ScholarField.VIEW_COUNT.toString()));
			int popularity = c.getInt(c.getColumnIndexOrThrow(ScholarField.POPULARITY.toString()));
			scholar = new Scholar(id, server_id, name, email, phone, page_url, 
					image_url, image_file, view_count, popularity);
		} catch (IllegalArgumentException e) {
			// TODO: remove error checking after testing
			throw new Error("column index does not exist.");
		}
		return scholar;
	}

	@Override
	public Scholar findByPk(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Scholar> findQuranScholars() {
		StatementSource stmt = new StatementSource() {

			@Override
			public String sql() {
				// FIXME this query returns all scholars in the table!!
				StringBuilder bldr;
				bldr = new StringBuilder("SELECT " + getScholarFields("sch")
						+ " FROM " + SCHOLAR_TABLE_NAME + " AS sch");
				bldr.append(" INNER JOIN " + SECTION_TABLE_NAME + " AS sec");
				bldr.append(" ON sch." + ScholarField.ID + " = sec."
						+ ScholarSectionsField.SCHOLAR_ID);
				bldr.append(" WHERE " + ScholarSectionsField.SECTION + " = '"
						+ Section.QURAN.toString() + "'");
				bldr.append(" ORDER BY " + ScholarField.NAME.toString());
				return bldr.toString();
			}

			@Override
			public String[] parameters() {
				return null;
			}
			
		};
		
		return (List<Scholar>) findMany(stmt);
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Scholar> findLessonsScholars() {
		StatementSource stmt = new StatementSource() {

			@Override
			public String sql() {
				// FIXME this query returns all scholars in the table!!
				StringBuilder bldr;
				bldr = new StringBuilder("SELECT " + getScholarFields("sch")
						+ " FROM " + SCHOLAR_TABLE_NAME + " AS sch");
				bldr.append(" INNER JOIN " + SECTION_TABLE_NAME + " AS sec");
				bldr.append(" ON sch." + ScholarField.ID + " = sec."
						+ ScholarSectionsField.SCHOLAR_ID);
				bldr.append(" WHERE " + ScholarSectionsField.SECTION + " = '"
						+ Section.LESSONS.toString() + "'");
				bldr.append(" ORDER BY " + ScholarField.NAME.toString());
				return bldr.toString();
			}

			@Override
			public String[] parameters() {
				return null;
			}
			
		};
		
		return (List<Scholar>) findMany(stmt);
	}
	
	@Override
	public void insert(@NonNull DomainObject obj, SQLiteDatabase db)
			throws SQLiteException {
		boolean isInTrans = false;
		if (!(obj instanceof Scholar)) {
			throw new Error("DomainObject not instance of Scholar");
		}
		Scholar scholar = (Scholar) obj;
		Log.d("ScholarMapper", String.format("Scholar server_id: %d", scholar.getServerId()));
		try {
			if (db == null || !db.isOpen()) {
				db = Repository.getInstance(mContext).getWritableDatabase();
				db.beginTransaction();
				isInTrans = true;
			}
			ContentValues values = new ContentValues();
			values.put(ScholarField.SERVER_ID.toString(), scholar.getServerId());
			values.put(ScholarField.NAME.toString(), scholar.getName());
			values.put(ScholarField.EMAIL.toString(), scholar.getEmail());
			values.put(ScholarField.PHONE.toString(), scholar.getPhone());
			values.put(ScholarField.PAGE_URL.toString(), scholar.getPageUrl());
			values.put(ScholarField.IMAGE_URL.toString(), scholar.getImageUrl());
			values.put(ScholarField.VIEW_COUNT.toString(), scholar.getViewCount());
			values.put(ScholarField.POPULARITY.toString(), scholar.getPopularity());
			
			// FIXME this causes the caller to exit premature during the syncing
			// due to a 'duplicate key constraint' exception.
			long scholar_id = db.insertWithOnConflict(SCHOLAR_TABLE_NAME, null,
					values, SQLiteDatabase.CONFLICT_REPLACE);
			
			Set<Section> sections = scholar.getSections();
			if (sections.size() > 0) {
				values.clear();
				for (Section section: sections) {
					values.put(ScholarSectionsField.SECTION.toString(), section.toString());
					values.put(ScholarSectionsField.SCHOLAR_ID.toString(),
							scholar_id);
					db.insertWithOnConflict(SECTION_TABLE_NAME, null, values,
							SQLiteDatabase.CONFLICT_REPLACE);
				}
			}
			if (isInTrans) {
				db.setTransactionSuccessful();
			}
		} finally {
			if (isInTrans) {
				db.endTransaction();
			}
		}
		
	}
}
