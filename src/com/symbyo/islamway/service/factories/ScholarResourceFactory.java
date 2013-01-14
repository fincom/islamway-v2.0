package com.symbyo.islamway.service.factories;

import org.eclipse.jdt.annotation.NonNull;

import com.symbyo.islamway.service.restclients.RestClient;
import com.symbyo.islamway.service.restclients.ScholarRestClient;

public class ScholarResourceFactory extends ResourceFactory {

	public ScholarResourceFactory(@NonNull String url_format) {
		super(url_format);
	}

	@SuppressWarnings("null")
	@Override
	public RestClient createRestClient() {
		
		return new ScholarRestClient(mUrlFormat);
	}

}
