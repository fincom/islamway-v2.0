package com.symbyo.islamway.service.restclients;

import java.util.Locale;

import org.eclipse.jdt.annotation.NonNull;

public class ScholarRestClient extends RestClient {

	public ScholarRestClient(@NonNull String url_format) {
		super(url_format);
	}

	protected void appendParameters() {
		if (mParams == null) {
			return;
		}
		// TODO: append the url parameters as key-value pairs to the mUrl.
	}

	@Override
	protected void prepareUrl() {
		if (mResourceId == RESOURCE_ID_NONE) {
			return;
		}
		mUrl = String.format(Locale.US, mUrlFormat, mResourceId);
		appendParameters();
	}

}
