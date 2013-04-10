package com.symbyo.islamway.service.factories;

import android.content.Context;
import com.symbyo.islamway.service.processors.Processor;
import com.symbyo.islamway.service.processors.QurranCollectionProcessor;
import com.symbyo.islamway.service.restclients.RestClient;

/**
 * @author kdehairy
 * @since 4/9/13
 */
public class QuranCollectionResourceFactory extends CollectionResourceFactory {

	public QuranCollectionResourceFactory( String url_format,
										   RestClient.HTTPMethod method,
										   int resource_id )
	{
		super( url_format, method, resource_id );
	}

	@Override
	public Processor createProcessor( Context context )
	{
		return new QurranCollectionProcessor( context, mResourceId );
	}
}
