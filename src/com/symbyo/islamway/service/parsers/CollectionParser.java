package com.symbyo.islamway.service.parsers;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.symbyo.islamway.domain.Collection;
import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.domain.Entry;
import junit.framework.Assert;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kdehairy
 * @since 2/21/13
 */
public class CollectionParser extends Parser {
    @Override
    protected List<Collection> doParse( String json )
    {
        Gson gson = new Gson();
        JSONCollection collection_raw = gson.fromJson( json,
                JSONCollection.class );
        List<Collection> result = new ArrayList<Collection>();
        result.add( collection_raw.toDomainObject() );
        return result;
    }

    @Override
    protected List<Collection> doParseCollection(
            String json )
    {
        Gson gson = new Gson();
        Type response_type = new TypeToken<JSONResponse<JSONCollection>>() {
        }.getType();
        JSONResponse<JSONCollection> response = gson.fromJson( json,
                response_type );
        List<Collection> result = new ArrayList<Collection>(
                response.getDomainObjects().size() );
        for ( JSONCollection collection_raw : response.getDomainObjects() ) {
            result.add( collection_raw.toDomainObject() );
        }
        return result;
    }

    private class JSONCollection extends JSONDomainObject<Collection> {

        @SerializedName("id")
        private int mServerId = INVALID_ID;

        @SerializedName("name")
        private String mTitle = null;

        @SerializedName("views_count")
        private int mViewsCount = 0;

        @SerializedName("entries_count")
        private int mEntriesCount = 0;

        @SerializedName( "type" )
        private String mEntrytype;

        @Override
        public Collection toDomainObject()
        {
            Entry.EntryType type = null;
            if ( mEntrytype.equals( "lessons_series" ) ) {
                type = Entry.EntryType.LESSON_SERIES;
            } else if ( mEntrytype.equals( "group" ) ) {
                type = Entry.EntryType.GROUP;
            } else if ( mEntrytype.equals( "mushaf" ) ) {
                type = Entry.EntryType.MUSHAF;
            } else if ( mEntrytype.equals( "lesson" ) ) {
                type = Entry.EntryType.LESSON;
            } else if ( mEntrytype.equals( "quran-recitation" ) ) {
                type = Entry.EntryType.QURAN_RECITATION;
            }
            Assert.assertNotNull( type );
            return new Collection( mServerId, mTitle, mViewsCount,
                    mEntriesCount, type );
        }
    }
}
