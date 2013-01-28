package com.symbyo.islamway.persistance.mappers;

import java.util.List;

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

public class ScholarMapper extends AbstractMapper implements IScholarFinder{
	
	public enum Material {
		QURAN,
		LESSON
	}
	
	public enum Field {
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
		Field(String name) {
			mName = name;
		}
		
		@Override
		public String toString() {
			return mName;
		}
	}
	
	public static final String TABLE_NAME = "scholar";

	private static String getFields() {
		StringBuilder bldr = new StringBuilder();
		Field[] fields = Field.values();
		for (int i = 0, len = fields.length; i< len; i++) {
			bldr.append(fields[i].toString());
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
		 private Field mField;
		 private String mValue;
		 
		 public FindByFieldValue(@NonNull Field field, @NonNull String value) {
			 mField = field;
			 mValue = value;
		 }

		@Override
		public String sql() {
			StringBuilder bldr;
			bldr = new StringBuilder("SELECT " + getFields() + "FROM " + TABLE_NAME);
			bldr.append(" WHERE " + mField.toString() + " = ?");
			bldr.append(" ORDER BY " + Field.NAME.toString());
			
			return bldr.toString();
		}

		@Override
		public String[] parameters() {
			return new String[] {mValue};
		}
		
	}
	
	static class FindQuranScholars implements StatementSource {

		@Override
		public String sql() {
			StringBuilder bldr;
			bldr = new StringBuilder("SELECT " + getFields() + "FROM " + TABLE_NAME);
			bldr.append(" ORDER BY " + Field.NAME.toString());
			return bldr.toString();
		}

		@Override
		public String[] parameters() {
			return null;
		}
		
	}

	@Override
	protected Scholar doLoad(@NonNull Cursor c) {
		Scholar scholar = null;
		try {
			int id = c.getInt(c.getColumnIndexOrThrow(Field.ID.toString()));
			int server_id = c.getInt(c.getColumnIndexOrThrow(Field.SERVER_ID.toString()));
			String name = c.isNull(c.getColumnIndexOrThrow(Field.NAME.toString())) ?
					null : c.getString(c.getColumnIndexOrThrow(Field.NAME.toString()));
			String email = c.isNull(c.getColumnIndexOrThrow(Field.EMAIL.toString())) ?
					null : c.getString(c.getColumnIndexOrThrow(Field.EMAIL.toString()));
			String phone = c.isNull(c.getColumnIndexOrThrow(Field.PHONE.toString())) ?
					null : c.getString(c.getColumnIndexOrThrow(Field.PHONE.toString()));
			String page_url = c.isNull(c.getColumnIndexOrThrow(Field.PAGE_URL.toString())) ?
					null : c.getString(c.getColumnIndexOrThrow(Field.PAGE_URL.toString()));
			String image_url = c.isNull(c.getColumnIndexOrThrow(Field.IMAGE_URL.toString())) ?
					null : c.getString(c.getColumnIndexOrThrow(Field.IMAGE_URL.toString()));
			String image_file = c.isNull(c.getColumnIndexOrThrow(Field.IMAGE_FILE.toString())) ?
					null : c.getString(c.getColumnIndexOrThrow(Field.IMAGE_FILE.toString()));
			int view_count = c.getInt(c.getColumnIndexOrThrow(Field.VIEW_COUNT.toString()));
			int popularity = c.getInt(c.getColumnIndexOrThrow(Field.POPULARITY.toString()));
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
				bldr = new StringBuilder("SELECT " + getFields() + " FROM " + TABLE_NAME);
				bldr.append(" ORDER BY " + Field.NAME.toString());
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
		if (!(obj instanceof Scholar)) {
			throw new Error("DomainObject not instance of Scholar");
		}
		Scholar scholar = (Scholar) obj;
		Log.d("ScholarMapper", String.format("Scholar server_id: %d", scholar.getServerId()));
		if (db == null || !db.isOpen()) {
			db = Repository.getInstance(mContext).getWritableDatabase();
		}
		ContentValues values = new ContentValues();
		values.put(Field.SERVER_ID.toString(), scholar.getServerId());
		values.put(Field.NAME.toString(), scholar.getName());
		values.put(Field.EMAIL.toString(), scholar.getEmail());
		values.put(Field.PHONE.toString(), scholar.getPhone());
		values.put(Field.PAGE_URL.toString(), scholar.getPageUrl());
		values.put(Field.IMAGE_URL.toString(), scholar.getImageUrl());
		values.put(Field.VIEW_COUNT.toString(), scholar.getViewCount());
		values.put(Field.POPULARITY.toString(), scholar.getPopularity());
		
		db.insertOrThrow(TABLE_NAME, null, values);
	}
}
