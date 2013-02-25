package com.symbyo.islamway.service.parsers;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.symbyo.islamway.domain.DomainObject;

public abstract class Parser {

    protected final int INVALID_ID = -1;

	public List<? extends DomainObject> parse( String json,
			boolean is_collection )
	{
		Log.d( "Parser", "parsing" );
		List<? extends DomainObject> result;
		if ( is_collection ) {
			result = doParseCollection( json );
		} else {
			result = doParse( json );
		}
		return result;
	}

	protected abstract List<? extends DomainObject> doParse( String json );

	protected abstract List<? extends DomainObject> doParseCollection(
			String json );

    protected class JSONResponse<T extends JSONDomainObject> {
        public final int INVALID = -1;

        @SerializedName("count")
        private int mCount = INVALID;

        @SerializedName("total_count")
        private int mTotalCount = INVALID;

        @SerializedName("items")
        private List<T> mItems;

        public List<T> getDomainObjects()
        {
            return mItems;
        }
    }

    protected abstract class JSONDomainObject<T extends DomainObject> {
        public abstract  T toDomainObject();
    }

}
