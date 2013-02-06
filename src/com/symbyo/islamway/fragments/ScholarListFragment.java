package com.symbyo.islamway.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.symbyo.islamway.R;
import com.symbyo.islamway.Searchable;
import com.symbyo.islamway.adapters.ScholarsAdapter;
import com.symbyo.islamway.domain.Section;

public class ScholarListFragment extends SherlockListFragment implements
		Searchable {

	public final static String	SECTION_KEY			= "section";
	private final int			SEARCH_MENU_ITEM_ID	= 1;

	private Section				mSection;
	private MenuItem			mSearchMenuItem;
	private ScholarsAdapter		mAdapter;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		// we set the retain instance to true so as not to populate the list
		// from the database everytime the configuration changes.
		setRetainInstance( true );
		setHasOptionsMenu( true );
		Log.d( "Islamway", "onCreate called" );
		super.onCreate( savedInstanceState );

		// get the section
		mSection = getArguments().getParcelable( SECTION_KEY );

		// get the quran scholars list from the database.
		new AsyncTask<Void, Void, ScholarsAdapter>() {

			@Override
			protected void onPostExecute( ScholarsAdapter result )
			{
				mAdapter = result;
				ScholarListFragment.this.setListAdapter( result );
			}

			@Override
			protected ScholarsAdapter doInBackground( Void... params )
			{
				if ( getActivity() == null ) {
					return null;
				}
				@SuppressWarnings("null")
				ScholarsAdapter adapter = new ScholarsAdapter( getActivity(),
						mSection );
				return adapter;
			}
		}.execute();
	}

	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater )
	{

		SearchView searchView = new SearchView( getSherlockActivity()
				.getSupportActionBar().getThemedContext() );
		searchView.setQueryHint( getString( R.string.menu_search_hint ) );

		// handle the query text change to live-filter the scholars list.
		searchView.setOnQueryTextListener( new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit( String query )
			{
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onQueryTextChange( String newText )
			{
				if (mAdapter != null) {
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

}
