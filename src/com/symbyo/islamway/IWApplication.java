package com.symbyo.islamway;

import android.app.Application;
import android.content.Context;
import android.util.SparseArray;
import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.persistance.Repository;
import junit.framework.Assert;

import java.util.List;

/**
 * @author kdehairy
 * @since 2/24/13
 */
public class IWApplication extends Application {

	private static SparseArray<List<? extends DomainObject>> mData;
	private static int mLatestKey = 0;

	/**
	 * Called when the application is starting, before any activity, service,
	 * or receiver objects (excluding content providers) have been created.
	 * Implementations should be as quick as possible (for example using
	 * lazy initialization of state) since the time spent in this function
	 * directly impacts the performance of starting the first activity,
	 * service, or receiver in a process.
	 * If you override this method, be sure to call super.onCreate().
	 */
	@Override
	public void onCreate()
	{
		super.onCreate();
		Context context = getApplicationContext();
		Repository.getInstance( context );
	}

	public static List<? extends DomainObject> readDomainObjects( int key )
	{
		if ( mData == null ) {
			return null;
		}
		return mData.get( key );
	}

	public static int putDomainObjects( List<? extends DomainObject> list )
	{
		Assert.assertNotNull( list );
		if ( mData == null ) {
			mData = new SparseArray<List<? extends DomainObject>>();
		}
		mData.put( ++mLatestKey, list );
		return mLatestKey;
	}
}
