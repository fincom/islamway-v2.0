package com.symbyo.islamway.service.parsers;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.symbyo.islamway.Utils;
import com.symbyo.islamway.domain.Collection;
import com.symbyo.islamway.domain.Entry;
import com.symbyo.islamway.domain.Lesson;
import com.symbyo.islamway.domain.Sura;
import junit.framework.Assert;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author kdehairy
 * @since 2/21/13
 */
public class CollectionParser extends Parser {
	@Override
	protected List<Entry> doParse( String json )
	{
		Gson gson = new Gson();
		JSONEntry collection_raw = gson.fromJson( json,
													   JSONEntry.class );
		List<Entry> result = new ArrayList<Entry>();
		result.add( collection_raw.toDomainObject() );
		return result;
	}

	@Override
	protected List<Entry> doParseCollection(
			String json )
	{
		Gson gson = new Gson();
		Type response_type = new TypeToken<JSONResponse<JSONEntry>>() {
		}.getType();
		JSONResponse<JSONEntry> response = gson.fromJson( json,
															   response_type );
		List<Entry> result = new ArrayList<Entry>(
				response.getDomainObjects().size() );
		for ( JSONEntry collection_raw : response.getDomainObjects() ) {
			result.add( collection_raw.toDomainObject() );
		}
		return result;
	}

	private class JSONEntry extends JSONDomainObject<Entry> {

		@SerializedName("id")
		private int mServerId = INVALID_ID;

		@SerializedName( "name" )
		private String mName = null;

		@SerializedName( "title" )
		private String mTitle = null;

		@SerializedName("views_count")
		private int mViewsCount = 0;

		@SerializedName("entries_count")
		private int mEntriesCount = 0;

		@SerializedName("type")
		private String mEntrytype = null;

		@Override
		public Entry toDomainObject()
		{
			Entry.EntryType type;
			Entry entry = null;
			Utils.FormatedLog( "Entry type: %s", mEntrytype );
			Assert.assertNotNull( mEntrytype );
			if ( mEntrytype.equals( "lessons_series" ) ) {
				type = Entry.EntryType.LESSON_SERIES;
				entry = new Collection( mServerId, mName, mViewsCount,
									   mEntriesCount, type );
			} else if ( mEntrytype.equals( "group" ) ) {
				type = Entry.EntryType.GROUP;
				entry = new Collection( mServerId, mName, mViewsCount,
									   mEntriesCount, type );
			} else if ( mEntrytype.equals( "mushaf" ) ) {
				type = Entry.EntryType.MUSHAF;
				entry = new Collection( mServerId, mName, mViewsCount,
									   mEntriesCount, type );
			} else if ( mEntrytype.equals( "lesson" ) ) {
				type = Entry.EntryType.LESSON;
				entry = new Lesson( mServerId, mTitle, mViewsCount, type );
			} else if ( mEntrytype.equals( "quran-recitation" ) ) {
				type = Entry.EntryType.QURAN_RECITATION;
				entry = new Sura( mServerId, mTitle, mViewsCount, type );
			}
			Assert.assertNotNull( entry );
			return entry;
		}
	}
}
