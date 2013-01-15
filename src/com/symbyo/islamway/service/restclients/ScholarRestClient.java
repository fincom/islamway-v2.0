package com.symbyo.islamway.service.restclients;

import java.util.Locale;

import org.eclipse.jdt.annotation.NonNull;

public class ScholarRestClient extends RestClient {

	public ScholarRestClient(@NonNull String url_format, RestClient.HTTPMethod http_method) {
		
		super(url_format, http_method);
	}

	protected void appendParameters() {
		if (mParams == null) {
			return;
		}
		// TODO: append the url parameters as key-value pairs to the mUrl.
	}

	@Override
	protected String prepareUrl() {
		if (mResourceId == RESOURCE_ID_NONE) {
			mUrl = mUrlFormat;
		} else {
			mUrl = String.format(Locale.US, mUrlFormat, mResourceId);
		}
		/*URL result = null;
		try {
			result = new URL(mUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new Error(e.getLocalizedMessage());
		}*/
		return mUrl;
	}

}
