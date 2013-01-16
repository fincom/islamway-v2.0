package com.symbyo.islamway.service.parsers;

import java.util.List;

import com.symbyo.islamway.domain.DomainObject;

public abstract class Parser {

	public List<? extends DomainObject> parse(String json, boolean is_collection) {
		List<? extends DomainObject> result = null;
		if (is_collection) {
			result = doParseCollection(json);
		} else {
			result = doParse(json);
		}
		return result;
	}

	protected abstract List<? extends DomainObject> doParse(String json);
	protected abstract List<? extends DomainObject> doParseCollection(String json);
	
}
