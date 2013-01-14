package com.symbyo.islamway.service.restclients;

import junit.framework.Assert;

import org.eclipse.jdt.annotation.NonNull;

import android.content.ContentValues;

public abstract class RestClient {

	protected final int RESOURCE_ID_NONE = -1;
	protected int mResourceId = RESOURCE_ID_NONE;
	protected ContentValues mParams;
	protected final String mUrlFormat;
	protected String mUrl;

	public RestClient(@NonNull final String url_format) {
		mUrlFormat = url_format;
	}

	/**
	 * 
	 * @param params
	 *            are inserted into the url-format in order of insertion of keys
	 * @return
	 */
	public RestClient setParameters(@NonNull ContentValues params) {
		mParams = params;
		return this;
	}

	public String getResponse() {
		prepareUrl();
		// TODO: execute the request
		return null;
	}

	protected abstract void prepareUrl();

	public void setResourceId(int resource_id) {
		Assert.assertTrue(resource_id > RESOURCE_ID_NONE);
		mResourceId = resource_id;
	}
}
