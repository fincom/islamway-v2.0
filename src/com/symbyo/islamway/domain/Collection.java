package com.symbyo.islamway.domain;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kdehairy
 * @since 2/20/13
 */
public class Collection extends Entry {


	private int mEntriesCount = 0;
	private ArrayList<Entry> mEntries;

	public Collection(
			int id, int server_id, String title, int entries_count,
			EntryType type )
	{
		this( id, server_id, title, entries_count, type, INVALID_ID );
	}

	public Collection(
			int id, int server_id, String title, int entries_count,
			EntryType type, int scholar_id )
	{
		super( id, server_id, title, type, scholar_id );
		mEntriesCount = entries_count;
	}

	public Collection(
			int server_id, String title, int entries_count, EntryType type )
	{
		this( server_id, title, entries_count, type, INVALID_ID );
	}

	public Collection(
			int server_id, String title, int entries_count, EntryType type,
			int scholar_id )
	{
		super( server_id, title, type, scholar_id );
		mEntriesCount = entries_count;
	}

	protected Collection( Parcel source )
	{
		super( source );
		mEntriesCount = source.readInt();
	}

	@Override
	protected void doWriteToParcel( Parcel dest )
	{
		dest.writeInt( getEntriesCount() );
	}

	public static final Creator<Collection> CREATOR =
			new Creator<Collection>() {

				@Override
				public Collection createFromParcel( Parcel source )
				{
					return new Collection( source );
				}

				@Override
				public Collection[] newArray( int size )
				{
					return new Collection[size];
				}
			};

	public int getEntriesCount()
	{
		return mEntriesCount;
	}

	public List<Entry> getEntries()
	{
		// TODO implement the method body
		return null;
	}
}
