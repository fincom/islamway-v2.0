package com.symbyo.islamway.service.parsers;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.symbyo.islamway.domain.Collection;
import com.symbyo.islamway.domain.DomainObject;

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
        ArrayList<Collection> result = new ArrayList<Collection>();
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
        ArrayList<Collection> result = new ArrayList<Collection>(
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

        @Override
        public Collection toDomainObject()
        {
            return new Collection( mServerId, mTitle, mViewsCount,
                    mEntriesCount );
        }
    }
}
