package com.symbyo.islamway.service.parsers;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.symbyo.islamway.Utils;
import com.symbyo.islamway.domain.Scholar;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScholarParser extends Parser {

	@Override
	protected List<Scholar> doParse( String json )
	{
		Gson gson = new Gson();
		JSONScholar scholar_raw = gson.fromJson( json, JSONScholar.class );
		List<Scholar> result = new ArrayList<Scholar>();
		result.add( scholar_raw.toDomainObject() );
		return result;
	}

	@Override
	protected List<Scholar> doParseCollection( String json )
	{
		Gson gson = new Gson();
		Type response_type = new TypeToken<JSONResponse<JSONScholar>>() {
		}.getType();
		JSONResponse<JSONScholar> response =
				gson.fromJson( json, response_type );
		ArrayList<Scholar> result = new ArrayList<Scholar>( response
																	.getDomainObjects()
																	.size() );
		for ( JSONScholar scholar_raw : response.getDomainObjects() ) {
			result.add( scholar_raw.toDomainObject() );
		}
		return result;
	}


	private class JSONScholar extends JSONDomainObject<Scholar> {

		@SerializedName( "id" )
		private int mServerId;

		@SerializedName( "name" )
		private String mName = null;

		@SerializedName( "email" )
		private String mEmail = null;

		@SerializedName( "phone" )
		private String mPhone   = null;
		private String mPageUrl = null;

		@SerializedName( "photo" )
		private String mImageUrl = null;

		private String mImageFile = null;

		@SerializedName( "views_count" )
		private int mViewCount = 0;

		@SerializedName( "popularity" )
		private int mPopularity = 0;

		@Override
		public Scholar toDomainObject()
		{
			Utils.FormatedLog( "parsed Scholar server_id: %d",
								   mServerId );
			return new Scholar( mServerId, mName, mEmail, mPhone, mPageUrl,
								mImageUrl, mImageFile, mViewCount,
								mPopularity );
		}
	}
}
