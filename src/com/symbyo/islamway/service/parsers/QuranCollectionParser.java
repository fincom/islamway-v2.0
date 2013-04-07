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
public class QuranCollectionParser extends Parser {
	@Override
	protected List<Entry> doParse( String json )
	{
		Gson gson = new Gson();
		JSONQuranCollection
				collection_raw = gson.fromJson( json, JSONQuranCollection.class );
		List<Entry> result = new ArrayList<Entry>();
		result.add( collection_raw.toDomainObject() );
		return result;
	}

	@Override
	protected List<Entry> doParseCollection(
			String json )
	{
		Gson gson = new Gson();
		Type response_type = new TypeToken<JSONResponse<JSONQuranCollection>>() {
		}.getType();
		JSONResponse<JSONQuranCollection> response = gson.fromJson( json,
															   response_type );
		List<Entry> result = new ArrayList<Entry>(
				response.getDomainObjects().size() );
		for ( JSONQuranCollection collection_raw : response.getDomainObjects() ) {
			result.add( collection_raw.toDomainObject() );
		}
		return result;
	}

}
