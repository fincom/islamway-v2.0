package com.symbyo.islamway.service.factories;

import org.eclipse.jdt.annotation.NonNull;

import android.content.Context;

import com.symbyo.islamway.service.parsers.Parser;
import com.symbyo.islamway.service.processors.OnPostProccessingListener;
import com.symbyo.islamway.service.processors.Processor;
import com.symbyo.islamway.service.restclients.RestClient;

public abstract class ResourceFactory {
	protected final String					mUrlFormat;
	protected final RestClient.HTTPMethod	mHTTPMethod;
	protected OnPostProccessingListener mPostProccessingListener;

	public ResourceFactory(@NonNull String url_format,
			RestClient.HTTPMethod http_method) {
		mUrlFormat = url_format;
		mHTTPMethod = http_method;
	}

	public abstract RestClient createRestClient();

	public abstract Parser createParser();

	public abstract Processor createProcessor( @NonNull Context context );

	public void setPostProccessingListener( OnPostProccessingListener listener )
	{
		mPostProccessingListener = listener;
	}
}
