package com.symbyo.islamway.service.factories;

import org.eclipse.jdt.annotation.NonNull;

import com.symbyo.islamway.service.parsers.Parser;
import com.symbyo.islamway.service.restclients.RestClient;

public abstract class ResourceFactory {
	protected final String mUrlFormat;
	protected final RestClient.HTTPMethod mHTTPMethod;
	public ResourceFactory(@NonNull String url_format, RestClient.HTTPMethod http_method) {
		mUrlFormat = url_format;
		mHTTPMethod = http_method;
	}
	public abstract RestClient createRestClient();
	public abstract Parser createParser();
}
