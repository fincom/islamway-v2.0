package com.symbyo.islamway.service.restclients;

import java.util.Locale;

import org.eclipse.jdt.annotation.NonNull;

/**
 * 
 * @author kdehairy
 * 
 */
public class ScholarRestClient extends RestClient {

	/**
	 * 70 is the max value accepted by the server
	 */
	private final int	ENTRIES_PER_PAGE	= 70;

	public ScholarRestClient(@NonNull String url_format,
			RestClient.HTTPMethod http_method) {

		super( url_format, http_method );
	}

	protected void appendParameters()
	{
		if ( mParams == null ) {
			return;
		}
		// TODO: append the url parameters as key-value pairs to the mUrl.
	}

	@Override
	protected String prepareUrl()
	{
		if ( mResourceId == RESOURCE_ID_NONE ) {
			mUrl = mUrlFormat;
		} else {
			mUrl = String.format( Locale.US, mUrlFormat, mResourceId );
		}
		mUrl += "?count=" + ENTRIES_PER_PAGE;

		return mUrl;
	}

}
