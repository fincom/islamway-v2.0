package com.symbyo.islamway.service;

import org.eclipse.jdt.annotation.NonNull;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;

import com.symbyo.islamway.service.factories.ResourceFactory;
import com.symbyo.islamway.service.factories.ScholarResourceFactory;
import com.symbyo.islamway.service.restclients.NetworkException;
import com.symbyo.islamway.service.restclients.Page;
import com.symbyo.islamway.service.restclients.Response;
import com.symbyo.islamway.service.restclients.RestClient;

/**
 * 'params' sent to this service through an intent EXTRA_PARAMS are of type 
 * ParamContainer.
 * @author kdehairy
 *
 */
public class IWService extends IntentService {

	private final String BASE_URL = "http://ar.islamway.net/api/";
	public static final String ACTION_GET_QURAN_SCHOLARS = "iw.service.get_quran_scholars";
	public static final String EXTRA_RESOURCE_ID = "resource_id";
	public static final String EXTRA_PARAMS = "params";
	public static final String EXTRA_CALLBACK_INTENT = "callback_intent";

	public enum Section {
		QURAN ("recitations"), 
		LESSONS ("lessons");

		private final String mValue;

		private Section(final String value) {
			mValue = value;
		}

		@Override
		public String toString() {
			return mValue;
		}
	}

	public enum Params {
		SECTION ("param_section"), 
		SCHOLAR_ID ("param_scholar_id"), 
		IS_COLLECTIONS_ONLY ("param_is_collections_only");

		private final String mValue;

		private Params(final String value) {
			mValue = value;
		}

		@Override
		public String toString() {
			return mValue;
		}
	}

	public IWService() {
		super("IslamWay");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		final String action = intent.getAction();
		final Intent pIntent = (Intent) intent
				.getParcelableExtra(EXTRA_CALLBACK_INTENT);
		final int resource_id = intent.getIntExtra(EXTRA_RESOURCE_ID, -1);
		final ContentValues params = (ContentValues) intent
				.getParcelableExtra(EXTRA_PARAMS);

		if (action == null) {
			throw new NullPointerException("intent action can't be null");
		}
		ResourceFactory factory = createResourceFactory(action);
		RestClient rest_client = factory.createRestClient();
		if (resource_id >= 0) {
			rest_client.setResourceId(resource_id);
		}
		if (params != null) {
			rest_client.setParameters(params);
		}
		Response response;
		try {
			response = rest_client.getResponse();
			for (Page page : response) {
				String responseText = page.getResponseText();

			}
		} catch (NetworkException e) {
			// TODO handle network errors
			e.printStackTrace();
			return;
		}
	}

	private @NonNull
	ResourceFactory createResourceFactory(@NonNull String action) {
		ResourceFactory result = null;
		String url_format = BASE_URL;
		if (action.equals(ACTION_GET_QURAN_SCHOLARS)) {
			/** < /recitations/scholars */
			url_format += Section.QURAN.toString() + "/scholars";
			result = new ScholarResourceFactory(url_format, RestClient.HTTPMethod.GET);
		}
		if (result == null) {
			throw new NullPointerException("ResourceFactory is null");
		}
		return result;
	}

}
