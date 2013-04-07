package com.symbyo.islamway.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.symbyo.islamway.IWApplication;
import com.symbyo.islamway.R;
import com.symbyo.islamway.ServiceHelper;
import com.symbyo.islamway.Utils;
import com.symbyo.islamway.adapters.EntryAdapter;
import com.symbyo.islamway.domain.Entry;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.List;

/**
 * @author kdehairy
 * @since 2/27/13
 */
public abstract class BaseEntryFragment extends SherlockListFragment
		implements
		Searchable {

	protected final String REQUEST_KEY = "request";
	/**
	 * This is the id of the latest request sent to the ServiceHelper.
	 */
	protected       int    mRequestId  = ServiceHelper.REQUEST_ID_NONE;
	protected EntryAdapter mAdapter;

	protected Crouton mCrouton;

	protected OnEntryItemClick mListener;
	protected MenuItem         mSearchMenuItem;
	protected final int SEARCH_MENU_ITEM_ID = 1;

	public static interface OnEntryItemClick {
		public void onEntryItemClick( Entry item );
	}

	@Override
	public void onSaveInstanceState( Bundle outState )
	{
		outState.putInt( REQUEST_KEY, mRequestId );
		super.onSaveInstanceState( outState );
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
		doOnCreate( savedInstanceState );
	}

	@Override
	public View onCreateView(
			LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState )
	{
		setActivityTitle( getSherlockActivity() );
		Crouton.cancelAllCroutons();
		if ( Utils.isNetworkAvailable( getSherlockActivity() ) ) {
			setListAdapter( mAdapter );
			if ( mAdapter == null ) {
				if ( isSavedLocally() ) {
					retrieveCollections();
				} else {
					requestCollections();
				}
			}
		} else {
			Crouton.makeText( getSherlockActivity(),
							  R.string.err_connect_network, Style.ALERT )
					.show();

			mAdapter = new EntryAdapter( getSherlockActivity(),
										 null );
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
						Entry collection = (Entry) getListAdapter()
								.getItem( position );
						mListener.onEntryItemClick( collection );
					}
				} );
	}

	@Override
	public void onAttach( Activity activity )
	{
		try {
			mListener = (OnEntryItemClick) activity;
		} catch ( ClassCastException e ) {
			throw new ClassCastException( activity.toString()
												  + " must implement OnEntryItemClick" );
		}
		if ( mCrouton != null ) {
			Crouton.hide( mCrouton );
		}
		Crouton.cancelAllCroutons();
		super.onAttach( activity );
	}

	private void requestCollections()
	{
		final LocalBroadcastManager mngr = LocalBroadcastManager
				.getInstance( getSherlockActivity() );
		mngr.registerReceiver( mCollectionRequestReceiver,
							   new IntentFilter(
									   ServiceHelper.ACTION_INVALIDATE_COLLECTION_LIST ) );
		ServiceHelper helper = ServiceHelper.getInstance(
				getSherlockActivity().getApplicationContext() );
		ServiceHelper.RequestState state = helper.getRequestState( mRequestId );

		if ( state == ServiceHelper.RequestState.NOT_REGISTERED ) {
			mRequestId = doRequestCollections( helper );

			mCrouton = Crouton.makeText( getSherlockActivity(),
										 R.string.info_syncing,
										 Utils.CROUTON_PROGRESS_STYLE );
			mCrouton.show();
		} else if ( state == ServiceHelper.RequestState.PENDING ) {
			mCrouton = Crouton.makeText( getSherlockActivity(),
										 R.string.info_syncing,
										 Utils.CROUTON_PROGRESS_STYLE );
			mCrouton.show();
		}
	}

	/**
	 * get the collections from the database.
	 */
	private void retrieveCollections()
	{
		AsyncTask<Void, Void, List<? extends Entry>> task =
			new AsyncTask<Void, Void, List<? extends Entry>>() {
				@Override
				protected List<? extends Entry> doInBackground( Void... params )
				{
					if ( getSherlockActivity() == null ) {
						return null;
					}
					return doRetrieveCollections();
				}

				@Override
				protected void onPostExecute( List data )
				{
					mAdapter = new EntryAdapter( getSherlockActivity(), data );
					setListAdapter( mAdapter );
				}
			};
		task.execute();
	}

	private BroadcastReceiver mCollectionRequestReceiver =
			new BroadcastReceiver() {
				@Override
				public void onReceive(
						Context context, Intent intent )
				{
					Utils.Log( "broadcast received." );
					final LocalBroadcastManager mngr = LocalBroadcastManager
							.getInstance( getSherlockActivity() );
					mngr.unregisterReceiver( this );
					int request_id =
							intent.getIntExtra( ServiceHelper.EXTRA_REQUEST_ID,
												ServiceHelper.REQUEST_ID_NONE );
					Utils.FormatedLog( "request id: %d", request_id );
					if ( request_id != mRequestId ) {
						return;
					}
					boolean error = intent.getBooleanExtra(
							ServiceHelper.EXTRA_RESPONSE_ERROR, false );
					Utils.FormatedLog( "error: %b", error );
					if ( error ) {
						if ( getSherlockActivity() != null ) {
							Crouton.hide( mCrouton );
							Crouton.makeText( getSherlockActivity(),
											  R.string.err_network,
											  Style.ALERT ).show();
						}
					}
					// TODO call retrieveCollections instead
					/*int key = intent.getIntExtra( ServiceHelper.EXTRA_DATA_KEY,
												  0 );
					Crouton.hide( mCrouton );
					@SuppressWarnings( "unchecked" )
					List<Entry> entries =
							(List<Entry>) IWApplication
									.readDomainObjects( key );
					mAdapter = new EntryAdapter( getSherlockActivity(),
												 entries );
					setListAdapter( mAdapter );*/
					Crouton.hide( mCrouton );
					retrieveCollections();
				}
			};

	@Override
	public void onCreateOptionsMenu(
			Menu menu, MenuInflater inflater )
	{
		SearchView search_view = new SearchView(
				getSherlockActivity().getSupportActionBar()
						.getThemedContext() );
		search_view.setQueryHint( getString(
				R.string.menu_quran_search_hint ) );
		search_view.setOnQueryTextListener(
				new SearchView.OnQueryTextListener() {
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
		mSearchMenuItem.setIcon( R.drawable.abs__ic_search )
				.setActionView( search_view )
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
	public void onDetach()
	{
		LocalBroadcastManager.getInstance( getSherlockActivity() )
				.unregisterReceiver( mCollectionRequestReceiver );
		super.onDetach();
	}

	protected abstract void doOnCreate( Bundle savedInstanceState );

	protected abstract int doRequestCollections( ServiceHelper helper );

	protected abstract void setActivityTitle( Activity activity );

	/**
	 * get the collections from the database.
	 */
	protected abstract List<? extends Entry> doRetrieveCollections();

	protected abstract boolean isSavedLocally();
}
