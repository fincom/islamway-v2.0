package com.symbyo.islamway.service.factories;

import org.eclipse.jdt.annotation.NonNull;

import com.symbyo.islamway.service.restclients.RestClient;

public abstract class ResourceFactory {
	protected final String mUrlFormat;
	public ResourceFactory(@NonNull String url_format) {
		mUrlFormat = url_format;
	}
	public abstract RestClient createRestClient();
}
