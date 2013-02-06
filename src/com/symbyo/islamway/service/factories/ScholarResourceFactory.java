package com.symbyo.islamway.service.factories;

import org.eclipse.jdt.annotation.NonNull;

import android.content.Context;

import com.symbyo.islamway.service.IWService;
import com.symbyo.islamway.service.parsers.Parser;
import com.symbyo.islamway.service.parsers.ScholarParser;
import com.symbyo.islamway.service.processors.Processor;
import com.symbyo.islamway.service.processors.ScholarProcessor;
import com.symbyo.islamway.service.restclients.RestClient;
import com.symbyo.islamway.service.restclients.ScholarRestClient;

public class ScholarResourceFactory extends ResourceFactory {
	IWService.Section	mSection;

	/**
	 * Initializes a ScholarResourceFactory. The default_section parameter is
	 * needed by the ScholarParser.
	 * 
	 * @param url_format
	 * @param http_method
	 * @param default_section
	 */
	public ScholarResourceFactory(@NonNull String url_format,
			RestClient.HTTPMethod http_method, IWService.Section default_section) {

		super( url_format, http_method );
		mSection = default_section;
	}

	@SuppressWarnings("null")
	@Override
	public RestClient createRestClient()
	{

		return new ScholarRestClient( mUrlFormat, mHTTPMethod );
	}

	@Override
	public Parser createParser()
	{
		return new ScholarParser();
	}

	@Override
	public Processor createProcessor( @NonNull Context context )
	{
		return new ScholarProcessor( context, mSection );
	}

}
