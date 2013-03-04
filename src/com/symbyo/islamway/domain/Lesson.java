package com.symbyo.islamway.domain;

import android.os.Parcel;

/**
 * @author kdehairy
 * @since 3/3/13
 */
public class Lesson extends Entry {
	public Lesson( int server_id, String title, int views_count,
				   EntryType type )
	{
		super( INVALID_ID, server_id, title, views_count, type );
	}

	@Override
	protected void doWriteToParcel( Parcel dest )
	{
	}

	protected Lesson( Parcel source )
	{
		super( source );
	}

	public static final Creator<Lesson> CREATOR = new Creator<Lesson>() {
		@Override
		public Lesson createFromParcel( Parcel source )
		{
			return new Lesson( source );
		}

		@Override
		public Lesson[] newArray( int size )
		{
			return new Lesson[size];
		}
	};
}
