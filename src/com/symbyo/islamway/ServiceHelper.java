package com.symbyo.islamway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseArray;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.domain.Section;
import com.symbyo.islamway.service.IWService;
import junit.framework.Assert;
import org.eclipse.jdt.annotation.NonNull;

public class ServiceHelper {

    public static final  int    REQUEST_ID_NONE                   = 0;
    public static final  String ACTION_INVALIDATE_SCHOLAR_LIST    = "iw.scholar_list_invalidate";
    public static final  String ACTION_INVALIDATE_COLLECTION_LIST = "iw.quran_collection_list_invalidate";
    private static final String ACTION_SERVICE_RESPONSE           = "iw.helper.service_response";
    public static final  String EXTRA_REQUEST_ID                  = "request_id";
    private static final String EXTRA_CALLBACK_ACTION             = "callback_action";
    public static final  String EXTRA_RESPONSE_ERROR              = "response_error";
    public static final  String EXTRA_DATA_KEY                    = "extra_data_key";

    private static ServiceHelper mInstance;
    private final  Context       mContext;
    private final Object                mLock          = new Object();
    private       int                   mLastRequestId = 0;
    private final SparseArray<Resource> mRequests      = new SparseArray<Resource>();

    public enum RequestState {
        NOT_REGISTERED,
        PENDING,
        FINISHED
    }

    private enum Resource {
        QURAN_SCHOLAR,
        LESSONS_SCHOLAR,
        SCHOLAR_QURAN_COLLECTION,
        SCHOLAR_LESSON_COLLECTION
    }

    public static synchronized ServiceHelper getInstance(
            @NonNull Context context )
    {
        if ( mInstance == null ) {
            mInstance = new ServiceHelper( context );
        }
        return mInstance;
    }

    private ServiceHelper( @NonNull Context context )
    {
        mContext = context;
    }

    private int getQuranScholars( int request_id )
    {
        Assert.assertTrue( request_id >= REQUEST_ID_NONE );

        int result;
        int index;
        RequestState state;
        if ( (index = mRequests.indexOfValue( Resource.QURAN_SCHOLAR )) >= 0 ) {
            result = mRequests.keyAt( index );
            state = RequestState.PENDING;
        } else if ( request_id == REQUEST_ID_NONE ) {
            synchronized ( mLock ) {
                result = ++mLastRequestId;
            }
            state = RequestState.NOT_REGISTERED;
        } else {
            result = request_id;
            state = getRequestState( request_id );
        }

        switch ( state ) {
            case FINISHED:
                Intent intent = new Intent( ACTION_INVALIDATE_SCHOLAR_LIST );
                LocalBroadcastManager.getInstance( mContext )
                        .sendBroadcast( intent );
            case PENDING:
                break;
            case NOT_REGISTERED:
                Log.d( "Islamway", "request queued" );
                mRequests.put( result, Resource.QURAN_SCHOLAR );

                sendRequestToService( IWService.ACTION_GET_QURAN_SCHOLARS,
                        ACTION_INVALIDATE_SCHOLAR_LIST,
                        result,
                        null );
        }
        return result;
    }

    private int getLessonsScholars( int request_id )
    {
        Assert.assertTrue( request_id >= REQUEST_ID_NONE );

        int result;
        int index;
        RequestState state;
        if ( (index = mRequests.indexOfValue(
                Resource.LESSONS_SCHOLAR )) >= 0 ) {
            result = mRequests.keyAt( index );
            state = RequestState.PENDING;
        } else if ( request_id == REQUEST_ID_NONE ) {
            synchronized ( mLock ) {
                result = ++mLastRequestId;
            }
            state = RequestState.NOT_REGISTERED;
        } else {
            result = request_id;
            state = getRequestState( request_id );
        }

        switch ( state ) {
            case FINISHED:
                Intent intent = new Intent( ACTION_INVALIDATE_SCHOLAR_LIST );
                LocalBroadcastManager.getInstance( mContext )
                        .sendBroadcast( intent );
            case PENDING:
                break;
            case NOT_REGISTERED:
                Log.d( "Islamway", "request queued" );
                mRequests.put( result, Resource.LESSONS_SCHOLAR );

                sendRequestToService( IWService.ACTION_GET_LESSONS_SCHOLARS,
                        ACTION_INVALIDATE_SCHOLAR_LIST,
                        result, null );
        }
        return result;
    }

    private int getScholarQuranCollection(
            int request_id, Scholar scholar, Section section )
    {
        Assert.assertTrue( request_id >= REQUEST_ID_NONE );

        int result;
        RequestState state;
        if ( request_id == REQUEST_ID_NONE ) {
            synchronized ( mLock ) {
                result = ++mLastRequestId;
            }
            state = RequestState.NOT_REGISTERED;
        } else {
            result = request_id;
            state = getRequestState( request_id );
        }

        switch ( state ) {
            case FINISHED:
                Intent intent = new Intent(
                        ACTION_INVALIDATE_COLLECTION_LIST );
                LocalBroadcastManager.getInstance( mContext )
                        .sendBroadcast( intent );
            case PENDING:
                break;
            case NOT_REGISTERED:
                if ( section.getType() == Section.SectionType.QURAN ) {
                    mRequests.put( result, Resource.SCHOLAR_QURAN_COLLECTION );

                    sendRequestToService(
                            IWService.ACTION_GET_SCHOLAR_QURAN_COLLECTION,
                            ACTION_INVALIDATE_COLLECTION_LIST,
                            result,
                            scholar.getServerId() );
                } else if ( section.getType() == Section.SectionType.LESSONS ) {
                    mRequests.put( result, Resource.SCHOLAR_LESSON_COLLECTION );

                    sendRequestToService(
                            IWService.ACTION_GET_SCHOLAR_LESSON_COLLECTION,
                            ACTION_INVALIDATE_COLLECTION_LIST,
                            result,
                            scholar.getServerId() );
                }

        }
        return result;
    }

    /**
     * @param action
     * @param request_id
     */
    private void sendRequestToService(
            String action, String callback_action, int request_id,
            Integer resource_id/*, ContentValues params*/ )
    {
        LocalBroadcastManager.getInstance( mContext ).registerReceiver(
                mServiceResponseReceiver,
                new IntentFilter( ACTION_SERVICE_RESPONSE ) );
        // build the pending intent.
        Intent pIntent = new Intent( ACTION_SERVICE_RESPONSE );
        pIntent.putExtra( EXTRA_REQUEST_ID, request_id );
        pIntent.putExtra( EXTRA_CALLBACK_ACTION, callback_action );

        // build the service intent and start the service.
        Intent intent = new Intent( mContext, IWService.class );
        intent.setAction( action );
        if ( resource_id != null ) {
            intent.putExtra( IWService.EXTRA_RESOURCE_ID,
                    resource_id.intValue() );
        }
        /*if ( params != null ) {
            intent.putExtra( IWService.EXTRA_PARAMS, params );
        }*/

        intent.putExtra( IWService.EXTRA_CALLBACK_INTENT, pIntent );
        mContext.startService( intent );
    }

    // Public Interface ///////////////////////////////////////////////////////

    /**
     * checks if the request id passed to it is registered or not. if not, but
     * was registered before, it resends the invalidate intent.
     *
     * @param request_id
     * @return
     */
    public RequestState getRequestState( int request_id )
    {
        Assert.assertTrue( request_id >= REQUEST_ID_NONE );
        RequestState state;
        int index = mRequests.indexOfKey( request_id );
        if ( index >= 0 ) {
            state = RequestState.PENDING;
            Log.d( "Islamway", "request pending" );
        } else if ( request_id == REQUEST_ID_NONE ) {
            state = RequestState.NOT_REGISTERED;
            Log.d( "Islamway", "request is not registered" );
        } else if ( request_id < mLastRequestId ) {
            state = RequestState.FINISHED;
            Log.d( "Islamway", "request has finished" );
        } else {
            state = RequestState.NOT_REGISTERED;
            Log.d( "Islamway", "request is not registered" );
        }
        return state;
    }

    private final
    BroadcastReceiver mServiceResponseReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive( Context context, Intent intent )
        {
            String action = intent.getStringExtra( EXTRA_CALLBACK_ACTION );
            int request_id = intent.getIntExtra( EXTRA_REQUEST_ID, -1 );
            boolean error = intent.getBooleanExtra(
                    IWService.EXTRA_RESPONSE_ERROR, false );
            int extra_data_key = intent.getIntExtra( IWService.EXTRA_DATA_KEY,
                    -1 );

            Intent i = new Intent( action );
            i.putExtra( EXTRA_REQUEST_ID, request_id );
            i.putExtra( EXTRA_DATA_KEY, extra_data_key );
            if ( error ) {
                i.putExtra( EXTRA_RESPONSE_ERROR, true );
            }
            LocalBroadcastManager.getInstance( mContext ).sendBroadcast( i );
            synchronized ( mLock ) {
                mRequests.remove( request_id );
            }
            LocalBroadcastManager.getInstance( mContext ).unregisterReceiver(
                    this );
        }

    };

    /**
     * Gets all scholars that have quran content.
     *
     * @return request id.
     */
    public int getQuranScholars()
    {
        return getQuranScholars( REQUEST_ID_NONE );
    }

    public int getLessonsScholars()
    {
        return getLessonsScholars( REQUEST_ID_NONE );
    }

    public int getScholarCollection( Scholar scholar, Section section )
    {
        return getScholarQuranCollection( REQUEST_ID_NONE, scholar, section );
    }
}
