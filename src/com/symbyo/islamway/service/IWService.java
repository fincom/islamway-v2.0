package com.symbyo.islamway.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.symbyo.islamway.service.factories.CollectionResourceFactory;
import junit.framework.Assert;
import org.eclipse.jdt.annotation.NonNull;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.domain.DomainObject.SyncState;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.domain.Section;
import com.symbyo.islamway.domain.Section.SectionType;
import com.symbyo.islamway.persistance.Repository;
import com.symbyo.islamway.persistance.mappers.ScholarMapper;
import com.symbyo.islamway.service.factories.ResourceFactory;
import com.symbyo.islamway.service.factories.ScholarResourceFactory;
import com.symbyo.islamway.service.parsers.Parser;
import com.symbyo.islamway.service.processors.OnPostProccessingListener;
import com.symbyo.islamway.service.processors.ProcessingException;
import com.symbyo.islamway.service.processors.Processor;
import com.symbyo.islamway.service.restclients.NetworkException;
import com.symbyo.islamway.service.restclients.Page;
import com.symbyo.islamway.service.restclients.Response;
import com.symbyo.islamway.service.restclients.RestClient;

/**
 * 'params' sent to this service through an intent EXTRA_PARAMS are of type
 * ParamContainer.
 *
 * @author kdehairy
 */
public class IWService extends IntentService {

    private final       String BASE_URL                            = "http://ar.islamway.net/api/";
    public static final String ACTION_GET_QURAN_SCHOLARS           = "iw.service.get_quran_scholars";
    public static final String ACTION_GET_LESSONS_SCHOLARS         = "iw.service.get_lessons_scholars";
    public static final String EXTRA_RESOURCE_ID                   = "resource_id";
    public static final String EXTRA_PARAMS                        = "params";
    public static final String EXTRA_CALLBACK_INTENT               = "callback_intent";
    public static final String EXTRA_RESPONSE_ERROR                = "response_error";
    public static       String ACTION_GET_SCHOLAR_QURAN_COLLECTION = "iw.service.get_scholar_quran_collection";

    public enum Params {
        SECTION( "param_section" ),
        SCHOLAR_ID( "param_scholar_id" ),
        IS_COLLECTIONS_ONLY( "param_is_collections_only" );

        private final String mValue;

        private Params( final String value )
        {
            mValue = value;
        }

        @Override
        public String toString()
        {
            return mValue;
        }
    }

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
        Assert.assertNotNull( parser );
        Assert.assertNotNull( processor );
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
            for ( Page page : response ) {
                Log.d( "IWService",
                        String.format( "page number: %d", page.getNumber() ) );
                String json = page.getResponseText();
                domain_collection.addAll( parser.parse( json,
                        response.isCollection() ) );
                Log.d( "IWService", "fetching next page" );
            }
            processor.process( domain_collection );
            Log.d( "IWService", "finished" );
        } catch ( NullPointerException e ) {
            /**
             * a network error has occurred during getting the next page, and
             * the page is null.
             */
            // showToast( getString( R.string.err_network ) );
            pIntent.putExtra( EXTRA_RESPONSE_ERROR, true );
            e.printStackTrace();
            return;
        } catch ( NetworkException e ) {
            // showToast( getString( R.string.err_network ) );
            pIntent.putExtra( EXTRA_RESPONSE_ERROR, true );
            e.printStackTrace();
            return;
        } catch ( ProcessingException e ) {
            // showToast( getString( R.string.err_processing_data ) );
            pIntent.putExtra( EXTRA_RESPONSE_ERROR, true );
            e.printStackTrace();
            return;
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
            // set the post processing listener, to update the section sync
            // state after processing.
            result.setPostProccessingListener( new OnPostProccessingListener() {

                @Override
                public void onPostProccessing( boolean result )
                {
                    if ( result ) {
                        ScholarMapper mapper = (ScholarMapper) Repository
                                .getInstance( IWService.this ).getMapper(
                                        Scholar.class );
                        mapper.updateSectionSyncState( section,
                                SyncState.SYNC_STATE_FULL );
                    }
                }
            } );
        } else if ( action.equals( ACTION_GET_LESSONS_SCHOLARS ) ) {
            final Section section = Repository.getInstance(
                    getApplicationContext() )
                                              .getSection(
                                                      SectionType.LESSONS );
            url_format += section.toString() + "/scholars";
            result = new ScholarResourceFactory( url_format,
                    RestClient.HTTPMethod.GET, section );
            // set the post processing listener, to update the section sync
            // state after processing.
            result.setPostProccessingListener( new OnPostProccessingListener() {

                @Override
                public void onPostProccessing( boolean result )
                {
                    if ( result ) {
                        ScholarMapper mapper = (ScholarMapper) Repository
                                .getInstance( IWService.this ).getMapper(
                                        Scholar.class );
                        mapper.updateSectionSyncState( section,
                                SyncState.SYNC_STATE_FULL );
                    }
                }
            } );
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
