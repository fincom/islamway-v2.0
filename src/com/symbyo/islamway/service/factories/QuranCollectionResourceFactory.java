package com.symbyo.islamway.service.factories;

import android.content.Context;
import com.symbyo.islamway.service.parsers.QuranCollectionParser;
import com.symbyo.islamway.service.parsers.Parser;
import com.symbyo.islamway.service.processors.QuranCollectionsProcessor;
import com.symbyo.islamway.service.processors.Processor;
import com.symbyo.islamway.service.restclients.RestClient;

/**
 * @author kdehairy
 * @since 2/20/13
 */
public class QuranCollectionResourceFactory extends ResourceFactory {
	private final int mResourceId;

	public QuranCollectionResourceFactory(
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
		return new QuranCollectionParser();
	}

	@Override
	public Processor createProcessor( Context context )
	{
		return new QuranCollectionsProcessor( context, mResourceId );
	}
}
