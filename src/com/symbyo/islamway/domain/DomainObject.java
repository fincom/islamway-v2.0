package com.symbyo.islamway.domain;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class DomainObject implements Parcelable {

	public static final int INVALID_ID = -1;

	protected int mId = INVALID_ID;

	protected DomainObject( int id )
	{
		mId = id;
	}

	public enum SyncState {
		SYNC_STATE_NONE,
		SYNC_STATE_BASIC,
		SYNC_STATE_FULL
	}

	protected DomainObject( Parcel source )
	{
		mId = source.readInt();
	}

	/**
	 * @return object id or INVALID_ID if it has none.
	 */
	public int getId()
	{
		return mId;
	}

	@Override
	public boolean equals( Object object )
	{
		if ( object != null && object instanceof DomainObject ) {
			DomainObject ormObject = (DomainObject) object;
			if ( ormObject.getId() == INVALID_ID || this.mId == INVALID_ID ) {
				return isEqual( ormObject );
			}
			if ( ormObject.getId() == this.mId ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return Integer.toString( mId ).hashCode();
	}

	@Override
	public int describeContents()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel( Parcel dest, int flags )
	{
		dest.writeInt( mId );
		doWriteToParcel( dest, flags );
	}

	protected abstract void doWriteToParcel( Parcel dest, int flags );

	protected abstract boolean isEqual( DomainObject object );
}
