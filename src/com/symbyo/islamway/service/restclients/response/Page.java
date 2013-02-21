package com.symbyo.islamway.service.restclients.response;

public class Page {
	private final int		mNumber;
	private final String	mResponse;

	public Page(int number, String response) {
		mNumber = number;
		mResponse = response;
	}

	public int getNumber()
	{
		return mNumber;
	}

	public String getResponseText()
	{
		return mResponse;
	}
}
