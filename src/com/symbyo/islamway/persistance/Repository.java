package com.symbyo.islamway.persistance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.persistance.mappers.AbstractMapper;
import com.symbyo.islamway.persistance.mappers.ScholarMapper;

public class Repository extends SQLiteOpenHelper {
	
	private final static String DATABASE_NAME = "data.sqlite";
	private final static int VERSION = 1;
	
	private final String CREATE_SCRIPT = "create_database.sql";
	
	private final Context mContext;
	private static Repository mInstance = null;
	
	private int mOpenConnections = 0;
	
	public synchronized static Repository getInstance(Context context) {
		Assert.assertNotNull(context);
		if (mInstance == null) {
			mInstance = new Repository(context);
		}
		return mInstance;
	}
	
	public synchronized static Repository getInstance() {
		if (mInstance == null) {
			return null;
		}
		return mInstance;
	}
	
	private Repository(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
		mContext = context;
	}
	
	/**
	 * strips sql script from comments and split it into array of statements.
	 * 
	 * @param input
	 *            inputstream with the sql script.
	 * @return array of strings of statements.
	 * @throws IOException
	 */
	private String[] parseSqlFile(InputStream input) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));

		String line;
		StringBuilder sql = new StringBuilder();
		String multiLineComment = null;

		while ((line = reader.readLine()) != null) {
			//line = line.trim();

			// Check for start of multi-line comment
			if (multiLineComment == null) {
				// Check for first multi-line comment type
				if (line.startsWith("/*")) {
					if (!line.endsWith("}"))
						multiLineComment = "/*";
					// Check for second multi-line comment type
				} else if (line.startsWith("{")) {
					if (!line.endsWith("}"))
						multiLineComment = "{";
					// Append line if line is not empty or a single line comment
				} else if (!line.startsWith("--") && !line.equals("")) {
					sql.append(line);
				} // Check for matching end comment
			} else if (multiLineComment.equals("/*")) {
				if (line.endsWith("*/"))
					multiLineComment = null;
				// Check for matching end comment
			} else if (multiLineComment.equals("{")) {
				if (line.endsWith("}"))
					multiLineComment = null;
			}
		}
		reader.close();
		return sql.toString().split(";");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		InputStream in_stream = null;
		try {
			// create the database.
			in_stream = mContext.getResources().getAssets().open(CREATE_SCRIPT);
			String[] statements = parseSqlFile(in_stream);
			for (String statement : statements) {
				db.execSQL(statement);
			}
			// insert test data.
			// TODO: remove the insert test data.
			/*in_stream.close();
			in_stream = null;
			in_stream = mContext.getResources().getAssets().open("insert_data.sql");
			statements = parseSqlFile(in_stream);
			for (String statement : statements) {
				db.execSQL(statement);
			}*/
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in_stream != null) {
					in_stream.close();
				}
			} catch (Exception e) {
			}
		}
	}
	
	@Override
	public synchronized void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		// increment the number of open connections.
		mOpenConnections++;
		if (!db.isReadOnly()) {
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase database, int old_version,
			int new_version) {
		onCreate(database);
	}
	
	/**
	 * implementation to avoid closing the database connection while it is in 
	 * use by others.
	 */
	@Override
	public synchronized void close() {
		mOpenConnections--;
		if (mOpenConnections == 0) {
			super.close();
		}
	}
	
	@SuppressWarnings("null")
	public <T> AbstractMapper getMapper(Class<T> clazz) {
		if (clazz.equals(Scholar.class)) {
			return new ScholarMapper(mContext);
		}
		return null;
	}
}
