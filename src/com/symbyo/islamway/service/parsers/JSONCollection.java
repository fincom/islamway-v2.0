package com.symbyo.islamway.service.parsers;

import com.google.gson.annotations.SerializedName;
import com.symbyo.islamway.Utils;
import com.symbyo.islamway.domain.Entry;
import junit.framework.Assert;

/**
 * The JSON Entry object.
 *
 * @author kdehairy
 * @since 4/7/13
 */
class JSONCollection extends Parser.JSONDomainObject<Entry> {

	@SerializedName( "id" )
	private int mServerId = Parser.INVALID_ID;

	@SerializedName( "name" )
	private String mName = null;

	@SerializedName( "title" )
	private String mTitle = null;

	@SerializedName( "entries_count" )
	private int mEntriesCount = 0;

	@SerializedName( "type" )
	private String mEntrytype = null;

	@SerializedName( "view_order" )
	private int mViewOrder = 0;

	@SerializedName( "narration" )
	private String mNarration = null;

	@Override
	public Entry toDomainObject()
	{
		int scholar_id = Parser.INVALID_ID;
		Entry.EntryType type;
		Entry entry = null;
		Utils.FormatedLog( "Entry type: %s", mEntrytype );
		// FIXME assertion fails with type 'quran recitation'. It appears to be missing from the response!
		Assert.assertNotNull( mEntrytype );
		if ( mEntrytype.equals( "lessons_series" ) ) {
			type = Entry.EntryType.LESSONS_SERIES;
			entry = new Entry( mServerId, mName, type );
			entry.setEntriesCount( mEntriesCount );
		} else if ( mEntrytype.equals( "group" ) ) {
			type = Entry.EntryType.GROUP;
			entry = new Entry( mServerId, mName, type );
			entry.setEntriesCount( mEntriesCount );
		} else if ( mEntrytype.equals( "mushaf" ) ) {
			type = Entry.EntryType.MUSHAF;
			entry = new Entry( mServerId, mName, type );
			entry.setEntriesCount( mEntriesCount );
		} else if ( mEntrytype.equals( "lesson" ) ) {
			type = Entry.EntryType.LESSON;
			entry = new Entry( mServerId, mTitle, type );
			entry.setEntriesCount( mEntriesCount );
		} else if ( mEntrytype.equals( "quran-recitation" ) ) {
			type = Entry.EntryType.QURAN_RECITATION;
			entry = new Entry( mServerId, mTitle, type );
			entry.setEntriesCount( mEntriesCount );
			entry.setViewOrder( mViewOrder );
			entry.setNarration( mNarration );
		}
		Assert.assertNotNull( entry );
		return entry;
	}
}
