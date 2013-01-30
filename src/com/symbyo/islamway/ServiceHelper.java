package com.symbyo.islamway;

import java.util.HashSet;

import junit.framework.Assert;

import org.eclipse.jdt.annotation.NonNull;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.symbyo.islamway.service.IWService;

public class ServiceHelper {
	
	private final int REQUEST_ID_NONE = 0;
	public static final String ACTION_INVALIDATE_SCHOLAR_LIST = "iw.scholar_list_invalidate";
	private static final String ACTION_SERVICE_RESPONSE = "iw.helper.service_response";
	private static final String EXTRA_REQUEST_ID = "request_id";
	private static final String EXTRA_CALLBACK_ACTION = "callback_action";
	
	private static ServiceHelper mInstance;
	private Context mContext;
	private HashSet<Integer> mRequests = new HashSet<Integer>();
	private int mLastRequestId = 0;
	
	public enum RequestState {
		NOT_REGISTERED,
		PENDING,
		FINISHED
	}
	
	public static synchronized ServiceHelper getInstance(@NonNull Context context) {
		if (mInstance == null) {
			mInstance = new ServiceHelper(context);
		}
		return mInstance;
	}
	
	private ServiceHelper(@NonNull Context context) {
		mContext = context;
	}
	
	private int getQuranScholars(int request_id) {
		Assert.assertTrue(request_id >= REQUEST_ID_NONE);
		int result = request_id;
		RequestState state = getRequestState(request_id);
		switch (state) {
		case FINISHED:
			Intent intent = new Intent(ACTION_INVALIDATE_SCHOLAR_LIST);
			LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
		case PENDING:
			result = request_id;
			break;
		case NOT_REGISTERED:
			synchronized (this) {
				request_id = ++mLastRequestId;
			}
			mRequests.add(Integer.valueOf(request_id));
			
			sendRequestToService(IWService.ACTION_GET_QURAN_SCHOLARS,
					request_id,  null, null);
			result = request_id;
		}

		return result;
	}
	
	/**
	 * 
	 * @param action
	 * @param request_id
	 * @param params
	 */
	private void sendRequestToService(String action, int request_id, Integer resource_id,
			ContentValues params) {
		//build the pending intent.
		Intent pIntent = new Intent(ACTION_SERVICE_RESPONSE);
		pIntent.putExtra(EXTRA_REQUEST_ID, request_id);
		pIntent.putExtra(EXTRA_CALLBACK_ACTION, ACTION_INVALIDATE_SCHOLAR_LIST);
		
		// build the service intent and start the service.
		Intent intent = new Intent(mContext, IWService.class);
		intent.setAction(action);
		if (resource_id != null) {
			intent.putExtra(IWService.EXTRA_RESOURCE_ID, resource_id.intValue());
		}
		if (params != null) {
			intent.putExtra(IWService.EXTRA_PARAMS, params);
		}

		intent.putExtra(IWService.EXTRA_CALLBACK_INTENT, pIntent);
		mContext.startService(intent);
	}
	
	// Public Interface ///////////////////////////////////////////////////////
	/**
	 * checks if the request id passed to it is registered or not. if not, but 
	 * was registered before, it resends the invalidate intent.
	 * @param request_id
	 * @return
	 */
	public RequestState getRequestState(int request_id) {
		Assert.assertTrue(request_id >= REQUEST_ID_NONE);
		RequestState state;
		if (request_id == REQUEST_ID_NONE) {
			state = RequestState.NOT_REGISTERED;
		} else if (mRequests.contains(Integer.valueOf(request_id))) {
			state = RequestState.PENDING;
		} else if (request_id < mLastRequestId) {
			state = RequestState.FINISHED;
		} else {
			state = RequestState.NOT_REGISTERED;
		}
		return state;
	}
	
	/**
	 * Gets all scholars that have quran content.
	 * @return request id.
	 */
	public int getQuranScholars() {
		return getQuranScholars(REQUEST_ID_NONE);
	}
}
