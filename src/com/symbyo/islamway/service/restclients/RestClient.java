package com.symbyo.islamway.service.restclients;

import android.content.ContentValues;
import com.symbyo.islamway.Utils;
import com.symbyo.islamway.service.restclients.response.Response;
import junit.framework.Assert;
import org.eclipse.jdt.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

public class RestClient {

	/**
	 * 70 is the max value accepted by the server
	 */
	private final int ENTRIES_PER_PAGE = 70;

	private final int TIME_OUT = 20000;

	protected final int RESOURCE_ID_NONE = -1;
	protected       int mResourceId      = RESOURCE_ID_NONE;
	//protected ContentValues	mParams;
	protected final String     mUrlFormat;
	protected final HTTPMethod mHTTPMethod;

	public enum HTTPMethod {
		GET( "GET" ),
		POST( "POST" );

		private final String mValue;

		private HTTPMethod( String value )
		{
			mValue = value;
		}

		@Override
		public String toString()
		{
			return mValue;
		}
	}

	public RestClient(
			@NonNull final String url_format, HTTPMethod http_method )
	{
		mUrlFormat = url_format;
		if ( http_method == null ) {
			http_method = HTTPMethod.GET;
		}
		mHTTPMethod = http_method;
	}

	/*/**
	 * 
	 * @param params
	 *            are inserted into the url-format in order of insertion of keys
	 * @return
	 */
	/*public RestClient setParameters( @NonNull ContentValues params )
	{
		mParams = params;
		return this;
	}*/

	public Response getResponse() throws NetworkException
	{
		HttpURLConnection conn = null;
		Response result = null;
		try {
			Utils.Log( prepareUrl( null ) );
			URL url = new URL( prepareUrl( null ) );
			/** < connect */
			conn = (HttpURLConnection) url.openConnection();
			/** < set the connection method */
			setHTTPMethod( conn );
			conn.setReadTimeout( TIME_OUT );
			/** < read the stream into mResponse */
			if ( conn.getResponseCode() == HttpURLConnection.HTTP_OK ) {
				result = new Response( this,
									   readResponse( conn.getInputStream() ) );
			}

		} catch ( MalformedURLException e ) {
			e.printStackTrace();
			throw new Error( e.getLocalizedMessage() );
		} catch ( IOException e ) {
			e.printStackTrace();
			NetworkException exp = new NetworkException();
			exp.setStackTrace( e.getStackTrace() );
			throw exp;
		} finally {
			if ( conn != null ) {
				conn.disconnect();
			}
		}
		return result;
	}

	public String getPage( int page_number ) throws NetworkException
	{
		HttpURLConnection conn = null;
		String result = null;
		try {
			ContentValues params = new ContentValues();
			params.put( "page", page_number );
			Utils.Log( prepareUrl( params ) );
			URL url = new URL( prepareUrl( params ) );
			/** < connect */
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout( TIME_OUT );
			/** < set the connection method */
			setHTTPMethod( conn );
			if ( conn.getResponseCode() == HttpURLConnection.HTTP_OK ) {
				result = readResponse( conn.getInputStream() );
			}

		} catch ( MalformedURLException e ) {
			e.printStackTrace();
			throw new Error( e.getLocalizedMessage() );
		} catch ( IOException e ) {
			e.printStackTrace();
			NetworkException exp = new NetworkException();
			exp.setStackTrace( e.getStackTrace() );
			throw exp;
		} finally {
			if ( conn != null ) {
				conn.disconnect();
			}
		}
		return result;
	}

	/**
	 * configures the HttpURLConnection with the HTTPMethod.
	 *
	 * @param connection
	 */
	private void setHTTPMethod( HttpURLConnection connection )
	{
		switch ( mHTTPMethod ) {
			case GET:
				break;
			case POST:
				connection.setDoOutput( true );
		}
		try {
			connection.setRequestMethod( mHTTPMethod.toString() );
		} catch ( ProtocolException e ) {
			e.printStackTrace();
			throw new Error( "method is not supported "
									 + "or set after the connection is established" );
		}
	}

	private String readResponse( InputStream in ) throws IOException
	{
		BufferedReader reader = null;
		String response = null;
		try {
			reader = new BufferedReader( new InputStreamReader( in ) );
			String line;
			StringBuilder bldr = new StringBuilder();
			while ( (line = reader.readLine()) != null ) {
				bldr.append( line );
			}
			response = bldr.toString();
		} finally {
			if ( reader != null ) {
				try {
					reader.close();
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	protected String prepareUrl( ContentValues params )
	{
		String url;
		if ( params == null ) {
			params = new ContentValues();
		}
		if ( mResourceId == RESOURCE_ID_NONE ) {
			url = mUrlFormat;
		} else {
			url = String.format( Locale.US, mUrlFormat, mResourceId );
		}
		params.put( "count", ENTRIES_PER_PAGE );
		url += "?";
		for ( Map.Entry<String, Object> entry : params.valueSet() ) {
			url += entry.getKey() + "=" + entry.getValue() + "&";
		}
		url = url.substring( 0, url.length() - 1 );

		return url;
	}

	public void setResourceId( int resource_id )
	{
		Assert.assertTrue( resource_id > RESOURCE_ID_NONE );
		mResourceId = resource_id;
	}
}
