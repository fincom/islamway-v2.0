package com.symbyo.islamway.service.parsers;

import com.google.gson.annotations.SerializedName;
import com.symbyo.islamway.Utils;
import com.symbyo.islamway.domain.Collection;
import com.symbyo.islamway.domain.Entry;
import com.symbyo.islamway.domain.Lesson;
import com.symbyo.islamway.domain.Sura;
import junit.framework.Assert;

/**
 * The JSON Entry object.
 * @author kdehairy
 * @since 4/7/13
 */
class JSONQuranCollection extends Parser.JSONDomainObject<Entry> {

	@SerializedName( "id" )
	private int mServerId = Parser.INVALID_ID;

	@SerializedName( "name" )
	private String mName = null;

	@SerializedName( "title" )
	private String mTitle = null;

	@SerializedName( "views_count" )
	private int mViewsCount = 0;

	@SerializedName( "entries_count" )
	private int mEntriesCount = 0;

	@SerializedName( "type" )
	private String mEntrytype = null;

	@Override
	public Entry toDomainObject()
	{
		int scholar_id = Parser.INVALID_ID;
		Entry.EntryType type;
		Entry entry = null;
		Utils.FormatedLog( "Entry type: %s", mEntrytype );
		Assert.assertNotNull( mEntrytype );
		if ( mEntrytype.equals( "lessons_series" ) ) {
			type = Entry.EntryType.LESSONS_SERIES;
			entry = new Collection( mServerId, mName, mEntriesCount, type, scholar_id );
		} else if ( mEntrytype.equals( "group" ) ) {
			type = Entry.EntryType.GROUP;
			entry = new Collection( mServerId, mName, mEntriesCount, type, scholar_id );
		} else if ( mEntrytype.equals( "mushaf" ) ) {
			type = Entry.EntryType.MUSHAF;
			entry = new Collection( mServerId, mName, mEntriesCount, type, scholar_id );
		} else if ( mEntrytype.equals( "lesson" ) ) {
			type = Entry.EntryType.LESSON;
			entry = new Lesson( mServerId, mTitle, mViewsCount, type, scholar_id );
		} else if ( mEntrytype.equals( "quran-recitation" ) ) {
			type = Entry.EntryType.QURAN_RECITATION;
			entry = new Sura( mServerId, mTitle, mViewsCount, type, scholar_id );
		}
		Assert.assertNotNull( entry );
		return entry;
	}
}
