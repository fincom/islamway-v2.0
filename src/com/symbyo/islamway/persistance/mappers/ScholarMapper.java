package com.symbyo.islamway.persistance.mappers;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import android.content.Context;
import android.database.Cursor;

import com.symbyo.islamway.domain.IScholarFinder;
import com.symbyo.islamway.domain.Scholar;

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
	
	static class FindByFieldValue implements IStatementSource {
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

	@Override
	protected Scholar doLoad(@NonNull Cursor c) {
		int id = c.getInt(c.getColumnIndex(Field.ID.toString()));
		int server_id = c.getInt(c.getColumnIndex(Field.SERVER_ID.toString()));
		String name = c.isNull(c.getColumnIndex(Field.NAME.toString())) ?
				null : c.getString(c.getColumnIndex(Field.NAME.toString()));
		String email = c.isNull(c.getColumnIndex(Field.EMAIL.toString())) ?
				null : c.getString(c.getColumnIndex(Field.EMAIL.toString()));
		String phone = c.isNull(c.getColumnIndex(Field.PHONE.toString())) ?
				null : c.getString(c.getColumnIndex(Field.PHONE.toString()));
		String page_url = c.isNull(c.getColumnIndex(Field.PAGE_URL.toString())) ?
				null : c.getString(c.getColumnIndex(Field.PAGE_URL.toString()));
		String image_url = c.isNull(c.getColumnIndex(Field.IMAGE_URL.toString())) ?
				null : c.getString(c.getColumnIndex(Field.IMAGE_URL.toString()));
		String image_file = c.isNull(c.getColumnIndex(Field.IMAGE_FILE.toString())) ?
				null : c.getString(c.getColumnIndex(Field.IMAGE_FILE.toString()));
		int view_count = c.getInt(c.getColumnIndex(Field.VIEW_COUNT.toString()));
		int popularity = c.getInt(c.getColumnIndex(Field.POPULARITY.toString()));
		
		return new Scholar(id, server_id, name, email, phone, page_url, 
				image_url, image_file, view_count, popularity);
	}

	@Override
	public Scholar findByPk(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<Scholar> findQuranScholars() {
		// TODO: return all scholars with quran material.
		return null;
		
	}

}
