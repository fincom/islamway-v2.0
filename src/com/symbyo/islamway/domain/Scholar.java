package com.symbyo.islamway.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class Scholar extends DomainObject {
	
	private final int mServerId;
	private final String mName;
	private final String mEmail;
	private final String mPhone;
	private final String mPageUrl;
	private final int mViewCount;
	private final int mPopularity;

	public Scholar(int id, int server_id, String name, String email, 
			String phone, String page_url, int view_count, int popularity) {
		super(id);
		mServerId = server_id;
		mName = name;
		mEmail = email;
		mPhone = phone;
		mPageUrl = page_url;
		mViewCount = view_count;
		mPopularity = popularity;
		
	}
	
	protected Scholar(Parcel source) {
		super(source);
		mServerId = source.readInt();
		mName = source.readString();
		mEmail = source.readString();
		mPhone = source.readString();
		mPageUrl = source.readString();
		mViewCount = source.readInt();
		mPopularity = source.readInt();
	}

	@Override
	protected void doWriteToParcel(Parcel dest, int flags) {
		dest.writeInt(mServerId);
		dest.writeString(mName);
		dest.writeString(mEmail);
		dest.writeString(mPhone);
		dest.writeString(mPageUrl);
		dest.writeInt(mViewCount);
		dest.writeInt(mPopularity);
	}
	
	public static final Parcelable.Creator<Scholar> CREATOR = new Parcelable.Creator<Scholar>() {

		@Override
		public Scholar createFromParcel(Parcel source) {
			return new Scholar(source);
		}

		@Override
		public Scholar[] newArray(int size) {	
			return new Scholar[size];
		}
	};

	public int getServerId() {
		return mServerId;
	}

	public String getName() {
		return mName;
	}

	public String getEmail() {
		return mEmail;
	}

	public String getPhone() {
		return mPhone;
	}

	public String getPageUrl() {
		return mPageUrl;
	}

	public int getViewCount() {
		return mViewCount;
	}

	public int getPopularity() {
		return mPopularity;
	}

}
