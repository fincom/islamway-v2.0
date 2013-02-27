package com.symbyo.islamway.fragments;

import android.app.Activity;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.symbyo.islamway.R;
import com.symbyo.islamway.Searchable;
import com.symbyo.islamway.ServiceHelper;
import com.symbyo.islamway.ServiceHelper.RequestState;
import com.symbyo.islamway.Utils;
import com.symbyo.islamway.adapters.ScholarsAdapter;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.domain.Section;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import junit.framework.Assert;

public class ScholarListFragment extends SherlockListFragment implements
        Searchable {

    public final static String SECTION_KEY         = "section";
    private final       int    SEARCH_MENU_ITEM_ID = 1;
    private final       String REQUEST_KEY         = "request";

    private Section            mSection;
    private MenuItem           mSearchMenuItem;
    private ScholarsAdapter    mAdapter;
    private OnScholarItemClick mListener;

    /**
     * flag raised when the database is being read. if it is set, any attempt to
     * read the database should wait.
     */
    private       boolean mIsReadingDatabase = false;
    private final Object  mLock              = new Object();

    /**
     * This is the id of the latest request sent to the ServiceHelper.
     */
    private int mRequestId = ServiceHelper.REQUEST_ID_NONE;

    private Crouton mCrouton;

    public static interface OnScholarItemClick {
        public void onLoadScholarCollectionsClick( Scholar scholar, Section section );
    }

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        // we set the retain instance to true so as not to populate the list
        // from the database everytime the configuration changes.
        setRetainInstance( true );
        setHasOptionsMenu( true );
        Log.d( "Islamway", "onCreate called" );
        super.onCreate( savedInstanceState );

        if ( savedInstanceState != null ) {
            mRequestId = savedInstanceState.getInt( REQUEST_KEY );
        }
        // get the section
        mSection = getArguments().getParcelable( SECTION_KEY );
        Assert.assertNotNull( mSection );
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState )
    {
        if ( mSection.getSyncState() == Section.SyncState.SYNC_STATE_FULL ) {
            // get the quran scholars list from the database.
            Log.d( "Islamway", "loading scholars form database" );
            retrieveScholars();
        } else if ( Utils.isNetworkAvailable( getSherlockActivity() ) ) {
            if ( mAdapter != null ) {
                mAdapter = null;
            }
            setListAdapter( mAdapter );
            requestScholars();
            Log.d( "Islamway", "fetching scholars from server." );

        } else {
            Crouton.makeText( getSherlockActivity(),
                    R.string.err_connect_network, Style.ALERT ).show();
            mAdapter = new ScholarsAdapter( getSherlockActivity(), mSection );
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
                        Scholar scholar = (Scholar) getListAdapter().getItem(
                                position );
                        mListener.onLoadScholarCollectionsClick( scholar, mSection );
                    }
                } );
    }

    @Override
    public void onAttach( Activity activity )
    {
        try {
            mListener = (OnScholarItemClick) activity;
        } catch ( ClassCastException e ) {
            throw new ClassCastException( activity.toString()
                    + " must implement OnScholarItemClick" );
        }
        if ( mCrouton != null ) {
            Crouton.hide( mCrouton );
        }
        Crouton.cancelAllCroutons();
        super.onAttach( activity );
    }

    private void requestScholars()
    {
        final LocalBroadcastManager mngr = LocalBroadcastManager
                .getInstance( getSherlockActivity() );
        mngr.registerReceiver( mScholarsRequestReceiver, new IntentFilter(
                ServiceHelper.ACTION_INVALIDATE_SCHOLAR_LIST ) );

        ServiceHelper helper = ServiceHelper.getInstance( getSherlockActivity()
                .getApplicationContext() );
        RequestState state = helper.getRequestState( mRequestId );

        if ( state == RequestState.NOT_REGISTERED ) {
            if ( mSection.getType() == Section.SectionType.QURAN ) {
                mRequestId = helper.getQuranScholars();
            } else {
                mRequestId = helper.getLessonsScholars();
            }

            mCrouton = Crouton.makeText( getSherlockActivity(),
                    R.string.info_syncing, Utils.CROUTON_PROGRESS_STYLE );
            mCrouton.show();
        } else if ( state == RequestState.PENDING ) {
            mCrouton = Crouton.makeText( getSherlockActivity(),
                    R.string.info_syncing, Utils.CROUTON_PROGRESS_STYLE );
            mCrouton.show();
        }
    }

    @Override
    public void onCreateOptionsMenu( Menu menu, MenuInflater inflater )
    {
        SearchView searchView = new SearchView( getSherlockActivity()
                .getSupportActionBar().getThemedContext() );

        searchView.setQueryHint( getString(
                R.string.menu_scholar_search_hint ) );

        // handle the query text change to live-filter the scholars list.
        searchView.setOnQueryTextListener( new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit( String query )
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange( String newText )
            {
                if ( mAdapter != null ) {
                    mAdapter.getFilter().filter( newText );
                }
                return false;
            }
        } );
        mSearchMenuItem = menu.add( Menu.NONE, SEARCH_MENU_ITEM_ID, Menu.NONE,
                R.string.menu_search );
        mSearchMenuItem
                .setIcon( R.drawable.abs__ic_search )
                .setActionView( searchView )
                .setShowAsAction(
                        MenuItem.SHOW_AS_ACTION_IF_ROOM
                                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW );

        super.onCreateOptionsMenu( menu, inflater );
    }

    @Override
    public void expandSearchView()
    {
        mSearchMenuItem.expandActionView();
    }

    @Override
    public void onSaveInstanceState( Bundle outState )
    {
        outState.putInt( REQUEST_KEY, mRequestId );
        super.onSaveInstanceState( outState );
    }

    private void retrieveScholars()
    {
        new AsyncTask<Void, Void, ScholarsAdapter>() {

            @Override
            protected void onPostExecute( ScholarsAdapter result )
            {
                // if the result is null and the database is being read, then
                // the retrieving failed.
                synchronized ( mLock ) {
                    if ( mIsReadingDatabase && result == null ) {
                        return;
                    }
                }
                mAdapter = result;
                ScholarListFragment.this.setListAdapter( result );
            }

            @Override
            protected ScholarsAdapter doInBackground( Void... params )
            {
                try {
                    int i = 0;
                    synchronized ( mLock ) {

                        while ( mIsReadingDatabase && i < 2 ) {
                            wait( 1000 );
                            i++;
                        }
                        // if the database is still being read, return
                        if ( mIsReadingDatabase ) {
                            return null;
                        }
                    }
                } catch ( InterruptedException e ) {
                    return null;
                }
                if ( getSherlockActivity() == null ) {
                    return null;
                }
                if ( mSection == null ) {
                    return null;
                }
                ScholarsAdapter adapter = new ScholarsAdapter(
                        getSherlockActivity(), mSection );
                synchronized ( mLock ) {
                    mIsReadingDatabase = false;
                }
                return adapter;
            }
        }.execute();
    }


    private final BroadcastReceiver mScholarsRequestReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive( Context context, Intent intent )
        {
            Log.d( "Islamway", "response received" );
            final LocalBroadcastManager mngr = LocalBroadcastManager
                    .getInstance( getSherlockActivity() );
            mngr.unregisterReceiver( this );
            int request_id = intent.getIntExtra(
                    ServiceHelper.EXTRA_REQUEST_ID,
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

            Crouton.hide( mCrouton );
            retrieveScholars();
        }

    };
    // @formatter:on

    @Override
    public void onDetach()
    {
        LocalBroadcastManager.getInstance( getSherlockActivity() )
                             .unregisterReceiver( mScholarsRequestReceiver );
        super.onDetach();
    }
}
