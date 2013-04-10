package com.symbyo.islamway.domain;

import android.os.Parcel;
import com.symbyo.islamway.persistance.Repository;
import com.symbyo.islamway.persistance.UnitOfWork;
import junit.framework.Assert;

import java.util.List;

/**
 * @author kdehairy
 * @since 2/27/13
 */
public class Entry extends DomainObject implements FilterableObject {

	private final int    mServerId;
	private final String mTitle;
	private int     mEntriesCount  = 0;
	private int     mScholarId     = INVALID_ID;
	private Scholar mScholar       = null;
	private int     mParentEntryId = INVALID_ID;
	private Entry   mParentEntry   = null;
	private String mPublishedAt;
	private int mViewOrder = 0;
	private       String    mNarration;
	private final EntryType mEntryType;

	public Scholar getScholar()
	{
		/*if ( mScholar == null && mScholarServerId != INVALID_ID ) {
			ScholarMapper mapper =
					(ScholarMapper) Repository.getInstance()
							.getMapper( Scholar.class );
			mScholar = mapper.findScholarByServerId( mScholarServerId );
		}*/
		return mScholar;
	}

	public int getScholarId()
	{
		return mScholarId;
	}

	public void setScholar( Scholar scholar )
	{
		mScholar = scholar;
		mScholarId = scholar.getId();
	}

	public Entry getParentEntry()
	{
		return mParentEntry;
	}

	public int getParentEntryId()
	{
		return mParentEntryId;
	}

	public String getPublishedAt()
	{
		return mPublishedAt;
	}

	public int getViewOrder()
	{
		return mViewOrder;
	}

	public String getNarration()
	{
		return mNarration;
	}

	public void setEntriesCount( int entry_count )
	{
		mEntriesCount = entry_count;
	}

	public void setScholarId( int scholar_id )
	{
		mScholarId = scholar_id;
	}

	public void setParentEntryId( int parent_entry_id )
	{
		mParentEntryId = parent_entry_id;
	}

	public void setParentEntry( Entry parent_entry )
	{
		mParentEntry = parent_entry;
		mParentEntryId = parent_entry.getId();
	}

	public void setPublishedAt( String published_at )
	{
		mPublishedAt = published_at;
	}

	public void setViewOrder( int view_order )
	{
		mViewOrder = view_order;
	}

	public void setNarration( String narration )
	{
		mNarration = narration;
	}

	public void setSyncState( SyncState sync_state )
	{
		EntryFinder mapper = (EntryFinder) Repository.getInstance().getMapper( Entry.class );
		mapper.updateSyncState( this, sync_state );

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

	public static interface EntryFinder {
		List<Entry> getScholarEntriesByTypes( final Scholar scholar,
											  final EntryType[] types );
		Entry findEntryByServerId( int mResourceId );

		void updateSyncState( Entry entry, SyncState sync_state );
	}

	public Entry(
			int id, int server_id, String title, EntryType type )
	{
		super( id );
		mServerId = server_id;
		mTitle = title;
		mEntryType = type;
		mScholarId = INVALID_ID;
	}

	public Entry( int server_id, String title, EntryType type )
	{
		this( INVALID_ID, server_id, title, type );
		/** < register the object as new */
		UnitOfWork.getCurrent().registerNew( this );
	}

	protected Entry( Parcel source )
	{
		super( source );
		mServerId = source.readInt();
		mTitle = source.readString();
		mEntriesCount = source.readInt();
		mScholarId = source.readInt();
		mParentEntryId = source.readInt();
		mPublishedAt = source.readString();
		mViewOrder = source.readInt();
		mNarration = source.readString();
		mEntryType = EntryType.values()[source.readInt()];


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
		dest.writeInt( mEntriesCount );
		dest.writeInt( mScholarId );
		dest.writeInt( mParentEntryId );
		dest.writeString( mPublishedAt );
		dest.writeInt( mViewOrder );
		dest.writeString( mNarration );
		dest.writeInt( mEntryType.ordinal() );
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

	public int getEntriesCount()
	{
		return mEntriesCount;
	}

	public static final Creator<Entry> CREATOR =
			new Creator<Entry>() {

				@Override
				public Entry createFromParcel( Parcel source )
				{
					return new Entry( source );
				}

				@Override
				public Entry[] newArray( int size )
				{
					return new Entry[size];
				}
			};
}
