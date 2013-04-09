package com.symbyo.islamway.service.parsers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.symbyo.islamway.domain.Entry;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kdehairy
 * @since 2/21/13
 */
public class CollectionParser extends Parser {
	@Override
	protected List<Entry> doParse( String json )
	{
		Gson gson = new Gson();
		JSONCollection
				collection_raw = gson.fromJson( json, JSONCollection.class );
		List<Entry> result = new ArrayList<Entry>();
		result.add( collection_raw.toDomainObject() );
		return result;
	}

	@Override
	protected List<Entry> doParseCollection(
			String json )
	{
		Gson gson = new Gson();
		Type response_type = new TypeToken<JSONResponse<JSONCollection>>() {
		}.getType();
		JSONResponse<JSONCollection> response = gson.fromJson( json,
															   response_type );
		List<Entry> result = new ArrayList<Entry>(
				response.getDomainObjects().size() );
		for ( JSONCollection collection_raw : response.getDomainObjects() ) {
			result.add( collection_raw.toDomainObject() );
		}
		return result;
	}

}
