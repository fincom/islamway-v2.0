package com.symbyo.islamway.service.restclients;

import java.util.Iterator;

public class Response implements Iterable<Page> {
	
	private final RestClient mClient;
	private final int mPagesNumber;
	private Page mCurrentPage;
	
	public Response(RestClient client, String response) {
		mClient = client;
		mPagesNumber = getPagesNumber();
		mCurrentPage = new Page(1, response);
	}

	@Override
	public Iterator<Page> iterator() {
		return new Iterator<Page>() {

			@Override
			public boolean hasNext() {
				return (mCurrentPage.getNumber() < mPagesNumber);
			}

			@Override
			public Page next() {
				String response = mClient.getPage(mCurrentPage.getNumber());
				mCurrentPage = new Page(mCurrentPage.getNumber() + 1, response);
				return mCurrentPage;
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
	
	private int getPagesNumber() {
		// TODO parse the count, page and total_count. 
		// TODO set the mPagesNumber to the pages number.
		return 1;
	}

}
