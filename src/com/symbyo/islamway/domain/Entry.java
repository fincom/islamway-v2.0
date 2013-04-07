package com.symbyo.islamway.domain;

import android.os.Parcel;
import com.symbyo.islamway.persistance.Repository;
import com.symbyo.islamway.persistance.UnitOfWork;
import com.symbyo.islamway.persistance.mappers.ScholarMapper;
import junit.framework.Assert;

import java.util.List;

/**
 * @author kdehairy
 * @since 2/27/13
 */
public abstract class Entry extends DomainObject implements FilterableObject {

	private final int       mServerId;
	private final String    mTitle;
	private final EntryType mEntryType;
	private       int       mScholarServerId;
	private       Scholar   mScholar = null;

	public Scholar getScholar()
	{
		if ( mScholar == null && mScholarServerId != INVALID_ID ) {
			ScholarMapper mapper =
					(ScholarMapper) Repository.getInstance()
							.getMapper( Scholar.class );
			mScholar = mapper.findScholarByServerId( mScholarServerId );
		}
		return mScholar;
	}

	public void setScholar( Scholar scholar ) {
		mScholar = scholar;
	}

	public enum EntryType {
		LESSONS_SERIES( "lessons_series" ),
		GROUP( "group" ),
		MUSHAF( "mushaf" ),
		LESSON( "lesson" ),
		QURAN_RECITATION( "quran_recitation" );

		private String mValue;

		EntryType( String value )
		{
			mValue = value;
		}

		@Override
		public String toString()
		{
			return mValue;
		}
	}

	public static interface ICollectionFinder {
		List<Collection> getScholarQuranCollections( Scholar scholar );

		List<Entry> getEntries( Collection collection );
	}

	public Entry(
			int id, int server_id, String title, EntryType type,
			int scholar_server_id )
	{
		super( id );
		mServerId = server_id;
		mTitle = title;
		mEntryType = type;
		mScholarServerId = scholar_server_id;

		/** < register the object as new */
		UnitOfWork.getCurrent().registerNew( this );
	}

	public Entry(
			int id, int server_id, String title, EntryType type )
	{
		this( id, server_id, title, type, INVALID_ID );
	}

	protected Entry( Parcel source )
	{
		super( source );
		mServerId = source.readInt();
		mTitle = source.readString();
		mEntryType = EntryType.values()[source.readInt()];
		mScholarServerId = source.readInt();

		/**
		 * register the object as new. if the original object was already in the
		 * UnitOfWork it won't be added again. Note that if the DomainObjects
		 * where not immutable, a bug will appear here. consider this case:
		 * object is registered as new with an instance object1. then passed
		 * through Parcel and a new instance of the object (object2) is created
		 * through this constructor. if there is made changes to object2 that
		 * needs to be saved, it will be ignored. simply because the equality
		 * test will reject the second instance on the bases that it is
		 * redundant. this will not affect this application, since the
		 * application does not manipulate the DomainObjects.
		 * */
		if ( mId == INVALID_ID ) {
			UnitOfWork.getCurrent().registerNew( this );
		}
	}

	@Override
	protected void doWriteToParcel( Parcel dest, int flags )
	{
		dest.writeInt( mServerId );
		dest.writeString( mTitle );
		dest.writeInt( mEntryType.ordinal() );
		dest.writeInt( mScholarServerId );
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
