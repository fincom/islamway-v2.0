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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.symbyo.islamway.*;
import com.symbyo.islamway.adapters.CollectionAdapter;
import com.symbyo.islamway.domain.Collection;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.List;

/**
 * @author kdehairy
 * @since 2/27/13
 */
public abstract class BaseCollectionFragment extends SherlockListFragment
		implements
		Searchable {

	protected final String REQUEST_KEY = "request";
	/**
	 * This is the id of the latest request sent to the ServiceHelper.
	 */
	protected       int    mRequestId  = ServiceHelper.REQUEST_ID_NONE;
	protected CollectionAdapter mAdapter;

	protected Crouton mCrouton;

	protected OnCollectionItemClick mListener;
	protected MenuItem              mSearchMenuItem;
	protected final int SEARCH_MENU_ITEM_ID = 1;

	public static interface OnCollectionItemClick {
		public void onCollectionItemClick( Collection item );
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
				requestCollections();
			}
		} else {
			Crouton.makeText( getSherlockActivity(),
							  R.string.err_connect_network, Style.ALERT )
					.show();

			mAdapter = new CollectionAdapter( getSherlockActivity(),
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
						Collection collection = (Collection) getListAdapter()
								.getItem( position );
						mListener.onCollectionItemClick( collection );
					}
				} );
	}

	@Override
	public void onAttach( Activity activity )
	{
		try {
			mListener = (OnCollectionItemClick) activity;
		} catch ( ClassCastException e ) {
			throw new ClassCastException( activity.toString()
												  + " must implement OnCollectionItemClick" );
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

	private BroadcastReceiver mCollectionRequestReceiver =
			new BroadcastReceiver() {
				@Override
				public void onReceive(
						Context context, Intent intent )
				{
					final LocalBroadcastManager mngr = LocalBroadcastManager
							.getInstance( getSherlockActivity() );
					mngr.unregisterReceiver( this );
					int request_id =
							intent.getIntExtra( ServiceHelper.EXTRA_REQUEST_ID,
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
											  R.string.err_network,
											  Style.ALERT ).show();
						}
					}
					int key = intent.getIntExtra( ServiceHelper.EXTRA_DATA_KEY,
												  0 );
					Crouton.hide( mCrouton );
					@SuppressWarnings("unchecked")
					List<Collection> collections =
							(List<Collection>) IWApplication
									.readDomainObjects( key );
					mAdapter = new CollectionAdapter( getSherlockActivity(),
													  collections );
					setListAdapter( mAdapter );
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
}
