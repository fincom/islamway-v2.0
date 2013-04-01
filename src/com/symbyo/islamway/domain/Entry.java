package com.symbyo.islamway.domain;

import android.os.Parcel;
import junit.framework.Assert;

import java.util.*;

/**
 * @author kdehairy
 * @since 2/27/13
 */
public abstract class Entry extends DomainObject implements FilterableObject {

	private final int       mServerId;
	private final String    mTitle;
	private final EntryType mEntryType;

	public enum EntryType {
		LESSONS_SERIES,
		GROUP,
		MUSHAF,
		LESSON,
		QURAN_RECITATION
	}

	public static interface IQuranCollectionFinder
	{
		List<Collection> getScholarQuranEntries( Scholar scholar );
	}

	public Entry(
			int id, int server_id, String title, EntryType type )
	{
		super( id );
		mServerId = server_id;
		mTitle = title;
		mEntryType = type;
	}

	protected Entry( Parcel source )
	{
		super( source );
		mServerId = source.readInt();
		mTitle = source.readString();
		mEntryType = EntryType.values()[source.readInt()];
	}

	@Override
	protected void doWriteToParcel( Parcel dest, int flags )
	{
		dest.writeInt( getServerId() );
		dest.writeString( getTitle() );
		dest.writeInt( mEntryType.ordinal() );
		doWriteToParcel( dest );
	}

	@Override
	protected boolean isEqual( DomainObject object )
	{
		if ( object != null && object instanceof Entry ) {
			Entry obj = (Entry) object;
			Assert.assertTrue( obj.getServerId() != INVALID_ID );
			Assert.assertTrue( this.getServerId() != INVALID_ID );
			if ( obj.getServerId() == this.mServerId ) {
				return true;
			}
		}
		return false;
	}

	public int getServerId()
	{
		return mServerId;
	}

	public String getTitle()
	{
		return mTitle;
	}

	public EntryType getType()
	{
		return mEntryType;
	}

	protected abstract void doWriteToParcel( Parcel dest );
}
