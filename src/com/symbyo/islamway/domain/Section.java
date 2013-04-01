package com.symbyo.islamway.domain;

import android.os.Parcel;
import android.os.Parcelable;
import com.symbyo.islamway.persistance.Repository;

import java.util.List;

public class Section extends DomainObject {

	public enum SectionType {
		QURAN( "recitations" ),
		LESSONS( "lessons" );

		private final String mValue;

		private SectionType( final String value )
		{
			mValue = value;
		}

		public int getId()
		{
			return ordinal() + 1;
		}

		@Override
		public String toString()
		{
			return mValue;
		}
	}

	private final SectionType mType;

	public Section( SectionType type )
	{
		super( type.getId() );
		mType = type;
	}

	protected Section( Parcel source )
	{
		super( source );
		mType = SectionType.values()[source.readInt()];
	}

	@Override
	protected void doWriteToParcel( Parcel dest, int flags )
	{
		dest.writeInt( mType.ordinal() );
	}

	@Override
	public boolean equals( Object object )
	{
		if ( object != null && object instanceof DomainObject ) {
			DomainObject ormObject = (DomainObject) object;
			return isEqual( ormObject );
		}
		return false;
	}

	@Override
	protected boolean isEqual( DomainObject object )
	{
		if ( object != null && object instanceof Section ) {
			Section obj = (Section) object;
			if ( obj.getType() == this.mType ) {
				return true;
			}
		}
		return false;
	}

	//@formatter:off
	public static final Parcelable.Creator<Section> CREATOR =
			new Parcelable.Creator<Section>() {

				@Override
				public Section createFromParcel( Parcel source )
				{
					return new Section( source );
				}

				@Override
				public Section[] newArray( int size )
				{
					return new Section[size];
				}
			};
	//@formatter:on

	@Override
	public String toString()
	{
		return mType.toString();
	}

	public SectionType getType()
	{
		return mType;
	}

	public List<Scholar> getSectionScholars()
	{
		Scholar.IScholarFinder mapper = (Scholar.IScholarFinder) Repository.getInstance()
				.getMapper( Scholar.class );
		return mapper.findScholarsBySection( this );
	}

	public SyncState getSyncState()
	{
		ISectionFinder mapper = (ISectionFinder) Repository.getInstance()
				.getMapper( Scholar.class );
		return mapper.getSectionSyncState( this );
	}

	public static interface ISectionFinder {

		public SyncState getSectionSyncState( Section section );

	}
}
