package com.symbyo.islamway.service.factories;

import com.symbyo.islamway.service.parsers.CollectionParser;
import com.symbyo.islamway.service.parsers.Parser;
import com.symbyo.islamway.service.restclients.RestClient;

/**
 * @author kdehairy
 * @since 2/20/13
 */
public abstract class CollectionResourceFactory extends ResourceFactory {
	protected final int mResourceId;

	public CollectionResourceFactory(
			String url_format, RestClient.HTTPMethod method, int resource_id )
	{
		super( url_format, method );
		mResourceId = resource_id;
	}

	@Override
	public RestClient createRestClient()
	{
		return new RestClient( mUrlFormat, mHTTPMethod );
	}

	@Override
	public Parser createParser()
	{
		return new CollectionParser();
	}
}
