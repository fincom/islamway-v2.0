package com.symbyo.islamway.service.restclients;

import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Response implements Iterable<Page> {
	
	private final RestClient mClient;
	private final int mPagesNumber;
	private Page mCurrentPage;
	private int returned_pages = 0;
	private boolean mIsCollection = false;
	
	public Response(RestClient client, String response) {
		mClient = client;
		mPagesNumber = getPagesNumber(response);
		mCurrentPage = new Page(1, response);
	}

	@Override
	public Iterator<Page> iterator() {
		return new Iterator<Page>() {

			@Override
			public boolean hasNext() {
				return (returned_pages < mPagesNumber);
			}

			@Override
			public Page next() {
				if (returned_pages == 0 ) {
					returned_pages++;
					/**< return the already fetched first page */
					return mCurrentPage;
				}
				String response;
				try {
					response = mClient.getPage(++returned_pages);
					mCurrentPage = new Page(returned_pages, response);
				} catch (NetworkException e) {
					e.printStackTrace();
					return null;
				}
				return mCurrentPage;
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
	
	private int getPagesNumber(String str) {
		Gson gson = new Gson();
		ResponseRaw response = gson.fromJson(str, ResponseRaw.class);
		mIsCollection = response.isCollection();
		return response.getPagesNumber();
	}
	
	public boolean isCollection() {
		return mIsCollection;
	}
	
	private static class ResponseRaw {
		public final int INVALID = -1;
		
		@SerializedName("count")
		private int mCount = INVALID;
		
		@SerializedName("total_count")
		private int mTotalCount = INVALID;
		
		public int getPagesNumber() {
			if (mCount == INVALID || mTotalCount == INVALID) {
				/**< this is a one object response */
				return 1;
			}
			return (int) Math.ceil((float)mTotalCount / (float)mCount);
		}
		
		public boolean isCollection() {
			return (mCount != INVALID);
		}
	}

}
