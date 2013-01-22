package com.symbyo.islamway.domain;

import junit.framework.Assert;

import com.symbyo.islamway.persistance.UnitOfWork;

import android.os.Parcel;
import android.os.Parcelable;

public class Scholar extends DomainObject {
	
	private final int mServerId;
	private final String mName;
	private final String mEmail;
	private final String mPhone;
	private final String mPageUrl;
	private final String mImageUrl;
	private final String mImageFile;
	private final int mViewCount;
	private final int mPopularity;

	public Scholar(int id, int server_id, String name, String email, 
			String phone, String page_url, String image_url, String image_file, int view_count, int popularity) {
		super(id);
		mServerId = server_id;
		mName = name;
		mEmail = email;
		mPhone = phone;
		mPageUrl = page_url;
		mImageUrl = image_url;
		mImageFile = image_file;
		mViewCount = view_count;
		mPopularity = popularity;
		
	}
	
	public Scholar(int server_id, String name, String email, 
			String phone, String page_url, String image_url, String image_file, int view_count, int popularity) {
		super(INVALID_ID);
		mServerId = server_id;
		mName = name;
		mEmail = email;
		mPhone = phone;
		mPageUrl = page_url;
		mImageUrl = image_url;
		mImageFile = image_file;
		mViewCount = view_count;
		mPopularity = popularity;
		
		/**< register the object as new */
		UnitOfWork.getCurrent().registerNew(this);
	}
	
	protected Scholar(Parcel source) {
		super(source);
		mServerId = source.readInt();
		mName = source.readString();
		mEmail = source.readString();
		mPhone = source.readString();
		mPageUrl = source.readString();
		mImageUrl = source.readString();
		mImageFile = source.readString();
		mViewCount = source.readInt();
		mPopularity = source.readInt();
		
		/** 
		 * register the object as new. if the original object was already in 
		 * the UnitOfWork it won't be added again.
		 * Note that if the DomainObjects  where not immutable, a bug will 
		 * appear here.
		 * consider this case:
		 * object is registered as new with an instance object1. then passed 
		 * through Parcel and a new instance of the object (object2) is created 
		 * through this constructor. if there is made changes to object2 that 
		 * needs to be saved, it will be ignored. simply because the equality 
		 * test will reject the second instance on the bases that it is redundant.
		 * this will not affect this application, since the application does not 
		 * manipulate the DomainObjects.
		 * */
		if (mId == INVALID_ID) {
			UnitOfWork.getCurrent().registerNew(this);
		}
	}

	@Override
	protected void doWriteToParcel(Parcel dest, int flags) {
		dest.writeInt(mServerId);
		dest.writeString(mName);
		dest.writeString(mEmail);
		dest.writeString(mPhone);
		dest.writeString(mPageUrl);
		dest.writeString(mImageUrl);
		dest.writeString(mImageFile);
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

	public String getImageUrl() {
		return mImageUrl;
	}

	public String getImageFile() {
		return mImageFile;
	}

	@Override
	protected boolean isEqual(DomainObject object) {
		if (object != null && object instanceof Scholar) {
			Scholar obj = (Scholar) object;
			Assert.assertTrue(obj.getServerId() != INVALID_ID);
			Assert.assertTrue(this.getServerId() != INVALID_ID);
			if (obj.getServerId() == this.mServerId) {
				return true;
			}
		}
		return false;
	}

}
