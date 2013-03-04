package com.symbyo.islamway.domain;

import android.os.Parcel;

/**
 * @author kdehairy
 * @since 3/3/13
 */
public class Sura extends Entry {
	public Sura( int server_id, String title, int views_count, EntryType type )
	{
		super( INVALID_ID, server_id, title, views_count, type );
	}

	@Override
	protected void doWriteToParcel( Parcel dest )
	{
	}

	protected Sura( Parcel source )
	{
		super( source );
	}

	public static final Creator<Sura> CREATOR = new Creator<Sura>() {
		@Override
		public Sura createFromParcel( Parcel source )
		{
			return new Sura( source );
		}

		@Override
		public Sura[] newArray( int size )
		{
			return new Sura[size];
		}
	};
}
