package com.symbyo.islamway.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.domain.Section;
import com.symbyo.islamway.domain.Section.SectionType;
import com.symbyo.islamway.persistance.Repository;
import com.symbyo.islamway.service.factories.CollectionResourceFactory;
import com.symbyo.islamway.service.factories.ResourceFactory;
import com.symbyo.islamway.service.factories.ScholarResourceFactory;
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
import java.util.Locale;

/**
 * @author kdehairy
 */
public class IWService extends IntentService {

    @SuppressWarnings("FieldCanBeLocal")
    private final       String BASE_URL                            = "http://ar.islamway.net/api/";
    public static final String ACTION_GET_QURAN_SCHOLARS           = "iw.service.get_quran_scholars";
    public static final String ACTION_GET_LESSONS_SCHOLARS         = "iw.service.get_lessons_scholars";
    public static final String EXTRA_RESOURCE_ID                   = "resource_id";
    public static final String EXTRA_CALLBACK_INTENT               = "callback_intent";
    public static final String EXTRA_RESPONSE_ERROR                = "response_error";
    public static final String EXTRA_DATA_KEY                      = "extra_data_key";
    public static final String ACTION_GET_SCHOLAR_QURAN_COLLECTION = "iw.service.get_scholar_quran_collection";

    public IWService()
    {
        super( "IslamWay" );
    }

    @Override
    protected void onHandleIntent( Intent intent )
    {
        final String action = intent.getAction();
        final Intent pIntent = (Intent) intent
                .getParcelableExtra( EXTRA_CALLBACK_INTENT );
        final int resource_id = intent.getIntExtra( EXTRA_RESOURCE_ID, -1 );
        Log.d( "Islamway", String.format( Locale.US, "Scholar server_id: %d",
                resource_id ) );
        /*final ContentValues params = (ContentValues) intent
                .getParcelableExtra( EXTRA_PARAMS );*/

        if ( action == null ) {
            throw new NullPointerException( "intent action can't be null" );
        }
        ResourceFactory factory = createResourceFactory( action );
        RestClient rest_client = factory.createRestClient();
        Parser parser = factory.createParser();
        Processor processor = factory.createProcessor( this );
        Assert.assertNotNull( rest_client );
        if ( resource_id >= 0 ) {
            rest_client.setResourceId( resource_id );
        }
        /*if ( params != null ) {
            rest_client.setParameters( params );
        }*/
        Response response;

        try {
            Log.d( "IWService", "start" );
            response = rest_client.getResponse();
            List<DomainObject> domain_collection = new LinkedList<DomainObject>();
            Assert.assertNotNull( parser );
            for ( Page page : response ) {
                Log.d( "IWService",
                        String.format( "page number: %d", page.getNumber() ) );
                String json = page.getResponseText();
                domain_collection.addAll( parser.parse( json,
                        response.isCollection() ) );
                Log.d( "IWService", "fetching next page" );
            }
            Assert.assertNotNull( processor );
            processor.process( domain_collection, pIntent );
            Log.d( "IWService", "finished" );
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
        }

    }

    private
    @NonNull
    ResourceFactory createResourceFactory( @NonNull String action )
    {
        ResourceFactory result = null;
        String url_format = BASE_URL;
        if ( action.equals( ACTION_GET_QURAN_SCHOLARS ) ) {
            /** /recitations/scholars */
            final Section section = Repository.getInstance(
                    getApplicationContext() ).getSection( SectionType.QURAN );
            url_format += section.toString() + "/scholars";
            result = new ScholarResourceFactory( url_format,
                    RestClient.HTTPMethod.GET, section );
        } else if ( action.equals( ACTION_GET_LESSONS_SCHOLARS ) ) {
            final Section section = Repository.getInstance(
                    getApplicationContext() )
                                              .getSection(
                                                      SectionType.LESSONS );
            url_format += section.toString() + "/scholars";
            result = new ScholarResourceFactory( url_format,
                    RestClient.HTTPMethod.GET, section );
        } else if ( action.equals( ACTION_GET_SCHOLAR_QURAN_COLLECTION ) ) {
            final Section section = Repository.getInstance(
                    getApplicationContext() ).getSection( SectionType.QURAN );
            url_format += section.toString() + "/scholar/%d/collections";
            result = new CollectionResourceFactory( url_format,
                    RestClient.HTTPMethod.GET, section );
        }
        if ( result == null ) {
            throw new NullPointerException( "ResourceFactory is null" );
        }
        return result;
    }

}
