package com.symbyo.islamway.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.symbyo.islamway.*;
import com.symbyo.islamway.adapters.ScholarQuranAdapter;
import com.symbyo.islamway.domain.Collection;
import com.symbyo.islamway.domain.Scholar;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import junit.framework.Assert;

import java.util.List;

/**
 * @author kdehairy
 * @since 2/20/13
 */
public class ScholarQuranCollectionFragment extends SherlockListFragment
        implements
        Searchable {

    public final static String SCHOLAR_KEY = "scholars";
    private final       String REQUEST_KEY = "request";

    /**
     * This is the id of the latest request sent to the ServiceHelper.
     */
    private int mRequestId = ServiceHelper.REQUEST_ID_NONE;
    private Scholar             mScholar;
    private ScholarQuranAdapter mAdapter;
    private OnQuranItemClick    mListener;

    private Crouton mCrouton;

    public static interface OnQuranItemClick {
        public void onQuranItemClick( Collection item );
    }

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setRetainInstance( true );
        setHasOptionsMenu( true );

        if ( savedInstanceState != null ) {
            mRequestId = savedInstanceState.getInt( REQUEST_KEY );
        }

        // get the scholar
        mScholar = (Scholar) getArguments().getParcelable( SCHOLAR_KEY );
        Assert.assertNotNull( mScholar );
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState )
    {
        if ( Utils.isNetworkAvailable( getSherlockActivity() ) ) {
            if ( mAdapter != null ) {
                mAdapter = null;
            }
            setListAdapter( mAdapter );
            requestQuranCollections();

        } else {
            Crouton.makeText( getSherlockActivity(),
                    R.string.err_connect_network, Style.ALERT ).show();

            mAdapter = new ScholarQuranAdapter( getSherlockActivity(), null );
            setListAdapter( mAdapter );
        }
        return super.onCreateView( inflater, container, savedInstanceState );
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState )
    {
        super.onActivityCreated( savedInstanceState );

        getListView().setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position,
                            long id )
                    {
                        Collection quran_collection = (Collection) getListAdapter()
                                .getItem(
                                        position );
                        mListener.onQuranItemClick( quran_collection );
                    }
                } );
    }

    @Override
    public void onAttach( Activity activity )
    {
        try {
            mListener = (OnQuranItemClick) activity;
        } catch ( ClassCastException e ) {
            throw new ClassCastException( activity.toString()
                    + " must implement OnQuranItemClick" );
        }
        if ( mCrouton != null ) {
            Crouton.hide( mCrouton );
        }
        Crouton.cancelAllCroutons();
        super.onAttach( activity );
    }

    private void requestQuranCollections()
    {
        // TODO request Collection collections from the server.
        final LocalBroadcastManager mngr = LocalBroadcastManager
                .getInstance( getSherlockActivity() );
        mngr.registerReceiver( mQuranCollectionRequestReceiver,
                new IntentFilter(
                        ServiceHelper.ACTION_INVALIDATE_QURAN_COLLECTION_LIST ) );
        ServiceHelper helper = ServiceHelper.getInstance(
                getSherlockActivity().getApplicationContext() );
        ServiceHelper.RequestState state = helper.getRequestState( mRequestId );

        if ( state == ServiceHelper.RequestState.NOT_REGISTERED ) {
            mRequestId = helper.getScholarQuranCollection( mScholar );

            mCrouton = Crouton.makeText( getSherlockActivity(),
                    R.string.info_syncing, Utils.CROUTON_PROGRESS_STYLE );
            mCrouton.show();
        } else if ( state == ServiceHelper.RequestState.PENDING ) {
            mCrouton = Crouton.makeText( getSherlockActivity(),
                    R.string.info_syncing, Utils.CROUTON_PROGRESS_STYLE );
            mCrouton.show();
        }
    }

    private BroadcastReceiver mQuranCollectionRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(
                Context context, Intent intent )
        {
            final LocalBroadcastManager mngr = LocalBroadcastManager
                    .getInstance( getSherlockActivity() );
            mngr.unregisterReceiver( this );
            int request_id = intent.getIntExtra( ServiceHelper.EXTRA_REQUEST_ID,
                    ServiceHelper.REQUEST_ID_NONE );
            if ( request_id != mRequestId ) {
                return;
            }
            boolean error = intent.getBooleanExtra(
                    ServiceHelper.EXTRA_RESPONSE_ERROR, false );
            if ( error ) {
                if ( getSherlockActivity() != null ) {
                    Crouton.hide( mCrouton );
                    Crouton.makeText( getSherlockActivity(),
                            R.string.err_network, Style.ALERT ).show();
                }
            }
            int key = intent.getIntExtra( ServiceHelper.EXTRA_DATA_KEY, 0 );
            Crouton.hide( mCrouton );
            List<Collection> collections = (List<Collection>) IWApplication
                    .readDomainObjects( key );
            mAdapter = new ScholarQuranAdapter( getSherlockActivity(), collections );
            setListAdapter( mAdapter );
        }
    };
    @Override
    public void expandSearchView()
    {
        // TODO implement searching
    }
}
