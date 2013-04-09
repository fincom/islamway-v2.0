package com.symbyo.islamway.domain;

import android.os.Parcel;

/**
 * @author kdehairy
 * @since 3/3/13
 */
public class Sura extends Entry {

	private final int mViewOrder;
	private final String mNarration;

	public Sura( int server_id, String title, int view_order, String narration, int scholar_id )
	{
		super( INVALID_ID, server_id, title, EntryType.QURAN_RECITATION,
			   scholar_id );
		mViewOrder = view_order;
		mNarration = narration;
	}

	@Override
	protected void doWriteToParcel( Parcel dest )
	{
		dest.writeInt( mViewOrder );
		dest.writeString( mNarration );
	}

	protected Sura( Parcel source )
	{
		super( source );
		mViewOrder = source.readInt();
		mNarration = source.readString();
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
