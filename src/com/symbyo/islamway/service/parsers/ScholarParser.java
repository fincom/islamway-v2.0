package com.symbyo.islamway.service.parsers;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.service.IWService.Section;


public class ScholarParser extends Parser {

	@Override
	protected List<Scholar> doParse(String json) {
		Gson gson = new Gson();
		JSONScholar scholar_raw = gson.fromJson(json, JSONScholar.class);
		ArrayList<Scholar> result = new ArrayList<Scholar>();
		result.add(scholar_raw.toScholar());
		return result;
	}
	
	@Override
	protected List<Scholar> doParseCollection(String json) {
		Gson gson = new Gson();
		JSONResponse response = gson.fromJson(json, JSONResponse.class);
		ArrayList<Scholar> result = new ArrayList<Scholar>(response.getScholars().size());
		for (JSONScholar scholar_raw : response.getScholars()) {
			result.add(scholar_raw.toScholar());
		}
		return result;
	}

	private static class JSONResponse {
		public final int INVALID = -1;

		@SerializedName("count")
		private int mCount = INVALID;

		@SerializedName("total_count")
		private int mTotalCount = INVALID;
		
		@SerializedName("items")
		private ArrayList<JSONScholar> mItems;
		
		public ArrayList<JSONScholar> getScholars() {
			return mItems;
		}
	}
	
	private static class JSONScholar {
		
		@SerializedName("id")
		private int mServerId;
		
		@SerializedName("name")
		private String mName = null;
		
		@SerializedName("email")
		private String mEmail = null;
		
		@SerializedName("phone")
		private String mPhone = null;
		private String mPageUrl = null;
		
		@SerializedName("photo")
		private String mImageUrl = null;
		
		private String mImageFile = null;
		
		@SerializedName("views_count")
		private int mViewCount = 0;
		
		@SerializedName("popularity")
		private int mPopularity = 0;
		
		public Scholar toScholar() {
			Log.d("Parser", String.format("parsed Scholar server_id: %d", mServerId));
			return new Scholar(mServerId, mName, mEmail, mPhone, mPageUrl,
					mImageUrl, mImageFile, mViewCount, mPopularity);
		}
	}
}
