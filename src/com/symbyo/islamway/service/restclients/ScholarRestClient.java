package com.symbyo.islamway.service.restclients;

import java.util.Locale;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

import android.content.ContentValues;

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

	/*protected void appendParameters()
	{
		if ( mParams == null ) {
			return;
		}
		// TODO: append the url parameters as key-value pairs to the mUrl.
	}*/

	@Override
	protected String prepareUrl(ContentValues params)
	{
		String url;
		if (params == null) {
			params = new ContentValues();
		}
		if ( mResourceId == RESOURCE_ID_NONE ) {
			url = mUrlFormat;
		} else {
			url = String.format( Locale.US, mUrlFormat, mResourceId );
		}
		params.put( "count", ENTRIES_PER_PAGE );
		url += "?";
		for (Map.Entry<String, Object> entry : params.valueSet()) {
			url += entry.getKey() + "=" + entry.getValue() + "&";
		}
		url = url.substring( 0, url.length() - 1  );

		return url;
	}

}
