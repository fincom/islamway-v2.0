package com.symbyo.islamway.domain;

import android.os.Parcel;
import com.symbyo.islamway.persistance.UnitOfWork;
import junit.framework.Assert;

import java.util.Locale;

/**
 * @author kdehairy
 * @since 2/20/13
 */
public class QuranCollection extends DomainObject {

    private final int    mServerId;
    private final String mTitle;
    private final int    mViewsCount;
    private final int    mEntriesCount;

    public QuranCollection(
            int id, int server_id, String title, int view_counts,
            int entries_count )
    {
        super( id );
        mServerId = server_id;
        mTitle = title;
        mViewsCount = view_counts;
        mEntriesCount = entries_count;
    }

    public QuranCollection(
            int server_id, String title, int view_counts,
            int entries_count )
    {
        super( INVALID_ID );
        mServerId = server_id;
        mTitle = title;
        mViewsCount = view_counts;
        mEntriesCount = entries_count;

        UnitOfWork.getCurrent().registerNew( this );
    }

    protected QuranCollection( Parcel source )
    {
        super( source );
        mServerId = source.readInt();
        mTitle = source.readString();
        mViewsCount = source.readInt();
        mEntriesCount = source.readInt();
        if ( mId == INVALID_ID ) {
            UnitOfWork.getCurrent().registerNew( this );
        }
    }

    @Override
    protected void doWriteToParcel( Parcel dest, int flags )
    {
        dest.writeInt( getServerId() );
        dest.writeString( getTitle() );
        dest.writeInt( getViewsCount() );
        dest.writeInt( getEntriesCount() );
    }

    public static final Creator<QuranCollection> CREATOR =
            new Creator<QuranCollection>() {

                @Override
                public QuranCollection createFromParcel( Parcel source )
                {
                    return new QuranCollection( source );
                }

                @Override
                public QuranCollection[] newArray( int size )
                {
                    return new QuranCollection[size];
                }
            };

    @Override
    protected boolean isEqual( DomainObject object )
    {
        if ( object != null && object instanceof QuranCollection ) {
            QuranCollection obj = (QuranCollection) object;
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
                this.getTitle() );
    }

    public int getServerId()
    {
        return mServerId;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public int getViewsCount()
    {
        return mViewsCount;
    }

    public int getEntriesCount()
    {
        return mEntriesCount;
    }
}
