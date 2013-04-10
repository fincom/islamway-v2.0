package com.symbyo.islamway.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import com.symbyo.islamway.Utils;
import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.domain.Section;
import com.symbyo.islamway.domain.Section.SectionType;
import com.symbyo.islamway.persistance.Repository;
import com.symbyo.islamway.service.factories.*;
import com.symbyo.islamway.service.parsers.Parser;
import com.symbyo.islamway.service.processors.ProcessingException;
import com.symbyo.islamway.service.processors.Processor;
import com.symbyo.islamway.service.restclients.NetworkException;
import com.symbyo.islamway.service.restclients.RestClient;
import com.symbyo.islamway.service.restclients.response.Page;
import com.symbyo.islamway.service.restclients.response.Response;
import junit.framework.Assert;
import org.eclipse.jdt.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

/**
 * @author kdehairy
 */
public class IWService extends IntentService {

	@SuppressWarnings("FieldCanBeLocal")
	private final       String BASE_URL                             =
			"http://ar.islamway.net/api/";
	public static final String ACTION_GET_QURAN_SCHOLARS            =
			"iw.service.get_quran_scholars";
	public static final String ACTION_GET_LESSONS_SCHOLARS          =
			"iw.service.get_lessons_scholars";
	public static final String EXTRA_RESOURCE_ID                    =
			"resource_id";
	public static final String EXTRA_CALLBACK_INTENT                =
			"callback_intent";
	public static final String EXTRA_RESPONSE_ERROR                 =
			"response_error";
	public static final String EXTRA_DATA_KEY                       =
			"extra_data_key";
	public static final String ACTION_GET_SCHOLAR_QURAN_COLLECTION  =
			"iw.service.get_scholar_quran_collection";
	public static final String ACTION_GET_SCHOLAR_LESSON_COLLECTION =
			"iw.service.get_scholar_lessons_collection";
	public static final String ACTION_GET_SUB_ENTRIES               =
			"iw.service.get_sub_collection";

	private PowerManager.WakeLock mWakeLock;

	public IWService()
	{
		super( "IslamWay" );
	}

	@Override
	protected void onHandleIntent( Intent intent )
	{
		if ( mWakeLock == null ) {
			PowerManager pm =
					(PowerManager) getSystemService( Context.POWER_SERVICE );
			mWakeLock = pm.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK,
										"network_wakelock" );
			mWakeLock.setReferenceCounted( false );
		}
		final String action = intent.getAction();
		final Intent pIntent = (Intent) intent
				.getParcelableExtra( EXTRA_CALLBACK_INTENT );
		final int resource_id = intent.getIntExtra( EXTRA_RESOURCE_ID, -1 );
		Utils.FormatedLog( "resource id: %d",
						   resource_id );
		Utils.Log( "action: " + action );

		if ( action == null ) {
			throw new NullPointerException( "intent action can't be null" );
		}
		ResourceFactory factory = createResourceFactory( action, resource_id );
		RestClient rest_client = factory.createRestClient();
		Parser parser = factory.createParser();
		Processor processor = factory.createProcessor( this );
		Assert.assertNotNull( rest_client );
		if ( resource_id >= 0 ) {
			rest_client.setResourceId( resource_id );
		}

		Response response;
		try {
			Utils.Log( "==start request==" );
			response = rest_client.getResponse();
			List<DomainObject> domain_collection =
					new LinkedList<DomainObject>();
			Assert.assertNotNull( parser );
			// TODO acquire a wakelock here
			mWakeLock.acquire();
			for ( Page page : response ) {
				Utils.FormatedLog( "page number: %d", page.getNumber() );
				String json = page.getResponseText();
				domain_collection.addAll( parser.parse( json,
														response.isCollection() ) );
				Utils.Log( "fetching next page" );
			}
			Assert.assertNotNull( processor );
			processor.process( domain_collection, pIntent );
			// TODO release the wakelock here
			mWakeLock.release();
			Utils.Log( "==finished==" );
		} catch ( NullPointerException e ) {
			/**
			 * a network error has occurred during getting the next page, and
			 * the page is null.
			 */
			// showToast( getString( R.string.err_network ) );
			pIntent.putExtra( EXTRA_RESPONSE_ERROR, true );
			e.printStackTrace();
		} catch ( NetworkException e ) {
			// showToast( getString( R.string.err_network ) );
			pIntent.putExtra( EXTRA_RESPONSE_ERROR, true );
			e.printStackTrace();
		} catch ( ProcessingException e ) {
			// showToast( getString( R.string.err_processing_data ) );
			pIntent.putExtra( EXTRA_RESPONSE_ERROR, true );
			e.printStackTrace();
		} finally {
			LocalBroadcastManager mngr = LocalBroadcastManager
					.getInstance( this );
			mngr.sendBroadcast( pIntent );
			if ( mWakeLock.isHeld() ) {
				mWakeLock.release();
			}
		}

	}

	private
	@NonNull
	ResourceFactory createResourceFactory( @NonNull String action,
										   int resource_id )
	{
		ResourceFactory result = null;
		String url_format = BASE_URL;
		if ( ACTION_GET_QURAN_SCHOLARS.equals( action ) ) {
			final Section section = Repository.getInstance(
					getApplicationContext() ).getSection( SectionType.QURAN );
			url_format += section.toString() + "/scholars";
			result = new ScholarResourceFactory( url_format,
												 RestClient.HTTPMethod.GET,
												 section );
		} else if ( ACTION_GET_LESSONS_SCHOLARS.equals( action ) ) {
			final Section section = Repository.getInstance(
					getApplicationContext() )
					.getSection(
							SectionType.LESSONS );
			url_format += section.toString() + "/scholars";
			result = new ScholarResourceFactory( url_format,
												 RestClient.HTTPMethod.GET,
												 section );
		} else if ( ACTION_GET_SCHOLAR_QURAN_COLLECTION.equals( action ) ) {
			final Section section = Repository.getInstance(
					getApplicationContext() ).getSection( SectionType.QURAN );
			url_format += section.toString() + "/scholar/%d/collections";
			result = new QuranCollectionResourceFactory( url_format,
														 RestClient.HTTPMethod.GET,
														 resource_id );
		} else if ( ACTION_GET_SCHOLAR_LESSON_COLLECTION.equals( action ) ) {
			final Section section = Repository.getInstance(
					getApplicationContext() ).getSection( SectionType.LESSONS );
			url_format += section.toString() + "/scholar/%d/collections";
			result = new LessonCollectionResourceFactory( url_format,
														  RestClient.HTTPMethod.GET,
														  resource_id );
		} else if ( ACTION_GET_SUB_ENTRIES.equals( action ) ) {
			url_format += "collection/%d/entries";
			result = new SubCollectionResourceFactory( url_format,
													   RestClient.HTTPMethod.GET,
													   resource_id );
		}

		if ( result == null ) {
			throw new NullPointerException( "ResourceFactory is null" );
		}
		return result;
	}

}
