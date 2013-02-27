package com.symbyo.islamway.domain;

import android.os.Parcel;
import com.symbyo.islamway.persistance.UnitOfWork;
import junit.framework.Assert;

import java.util.Locale;

/**
 * @author kdehairy
 * @since 2/20/13
 */
public class Collection extends Entry implements FilterableObject {


    private final int    mEntriesCount;

    public Collection(
            int id, int server_id, String title, int view_counts,
            int entries_count )
    {
        super( id, server_id, title, view_counts );
        mEntriesCount = entries_count;
    }

    public Collection(
            int server_id, String title, int view_counts,
            int entries_count )
    {
        super( INVALID_ID, server_id, title, view_counts );
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
}