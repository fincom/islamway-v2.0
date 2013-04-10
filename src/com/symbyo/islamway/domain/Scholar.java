package com.symbyo.islamway.domain;

import android.os.Parcel;
import android.os.Parcelable;
import com.symbyo.islamway.persistance.Repository;
import com.symbyo.islamway.persistance.UnitOfWork;
import junit.framework.Assert;
import org.eclipse.jdt.annotation.NonNull;

import java.util.*;

public class Scholar extends DomainObject implements FilterableObject {

	private final int    mServerId;
	private final String mName;
	private final String mImageUrl;
	private final String mImageFileName;

	private final Set<Section> mSections = new HashSet<Section>( 2 );
	private List<Entry> mQuranCollections;

	public List<Entry> getCollectionsBySection( Section section )
	{
		if ( mQuranCollections == null ) {
			mQuranCollections = new ArrayList<Entry>();
			Entry.EntryFinder mapper =
					(Entry.EntryFinder) Repository.getInstance()
							.getMapper( Entry.class );
			List<Entry> list = null;
			switch ( section.getType() ) {
				case QURAN: {
					Entry.EntryType[] types = {
							Entry.EntryType.MUSHAF,
							Entry.EntryType.QURAN_RECITATION
					};
					list = mapper.getScholarEntriesByTypes( this, types );
					break;
				}
				case LESSONS: {
					Entry.EntryType[] types = {
							Entry.EntryType.GROUP,
							Entry.EntryType.LESSONS_SERIES,
							Entry.EntryType.LESSON
					};
					list = mapper.getScholarEntriesByTypes( this, types );
				}
			}

			if ( list != null ) {
				mQuranCollections.addAll( list );
			}
		}
		return mQuranCollections;
	}

	public static interface IScholarFinder {

		public List<Scholar> findScholarsBySection( Section section );

		SyncState getQuranSyncState( Scholar scholar );

		SyncState getLessonsSyncState( Scholar scholar );

		Scholar findScholarByServerId( int server_id );

		void updateScholarQuranSyncState( Scholar scholar, SyncState state );

		void updateScholarLessonSyncState( Scholar scholar, SyncState state );
	}

	public Scholar(
			int id, int server_id, String name, String image_url,
			String image_file )
	{
		super( id );
		mServerId = server_id;
		mName = name;
		mImageUrl = image_url;
		mImageFileName = image_file;

	}

	public Scholar(
			int server_id, String name, String image_url, String image_file )
	{
		super( INVALID_ID );
		mServerId = server_id;
		mName = name;
		mImageUrl = image_url;
		mImageFileName = image_file;

		/** < register the object as new */
		UnitOfWork.getCurrent().registerNew( this );
	}

	protected Scholar( Parcel source )
	{
		super( source );
		mServerId = source.readInt();
		mName = source.readString();
		mImageUrl = source.readString();
		mImageFileName = source.readString();

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
		dest.writeString( mName );
		dest.writeString( mImageUrl );
		dest.writeString( mImageFileName );
	}

	//@formatter:off
	public static final Parcelable.Creator<Scholar> CREATOR =
			new Parcelable.Creator<Scholar>() {

				@Override
				public Scholar createFromParcel( Parcel source )
				{
					return new Scholar( source );
				}

				@Override
				public Scholar[] newArray( int size )
				{
					return new Scholar[size];
				}
			};
	//@formatter:on

	public int getServerId()
	{
		return mServerId;
	}

	public String getName()
	{
		return mName;
	}

	public String getImageUrl()
	{
		return mImageUrl;
	}

	public String getImageFileName()
	{
		return mImageFileName;
	}

	public void addSection( @NonNull Section section )
	{
		mSections.add( section );
	}

	public Set<Section> getSections()
	{
		return Collections.unmodifiableSet( mSections );
	}

	@Override
	protected boolean isEqual( DomainObject object )
	{
		if ( object != null && object instanceof Scholar ) {
			Scholar obj = (Scholar) object;
			Assert.assertTrue( obj.getServerId() != INVALID_ID );
			Assert.assertTrue( this.getServerId() != INVALID_ID );
			if ( obj.getServerId() == this.mServerId ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString()
	{
		return String.format( Locale.US, "id: %d, name: %s", this.getId(),
							  this.getName() );
	}

	@Override
	public String getTitle()
	{
		return getName();
	}

	public SyncState getQuranSyncState()
	{
		IScholarFinder mapper = (IScholarFinder) Repository.getInstance()
				.getMapper( Scholar.class );
		return mapper.getQuranSyncState( this );
	}

	public void setQuranSyncState( SyncState state )
	{
		IScholarFinder mapper = (IScholarFinder) Repository.getInstance()
				.getMapper( Scholar.class );
		mapper.updateScholarQuranSyncState( this, state );
	}

	public void setLessonSyncState( SyncState sync_state )
	{
		IScholarFinder mapper = (IScholarFinder) Repository.getInstance()
				.getMapper( Scholar.class );
		mapper.updateScholarLessonSyncState( this, sync_state );
	}

	public SyncState getLessonsSyncState()
	{
		IScholarFinder mapper = (IScholarFinder) Repository.getInstance()
				.getMapper( Scholar.class );
		return mapper.getLessonsSyncState( this );
	}
}
