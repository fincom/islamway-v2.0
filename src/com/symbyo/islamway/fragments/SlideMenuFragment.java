package com.symbyo.islamway.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.symbyo.islamway.OnSlideMenuItemClick;
import com.symbyo.islamway.R;

public class SlideMenuFragment extends SherlockFragment {

	private final String			ITEM_TYPE_KEY	= "item_type";
	private OnSlideMenuItemClick	mListener;
	private MenuItemType			mCurrentMenuItemType;

	// same order as in the array adapter.
	public enum MenuItemType {
		QURAN,
		LESSONS,
		PLAYING_LIST
	}

	@Override
	public void onSaveInstanceState( Bundle outState )
	{
		outState.putInt( ITEM_TYPE_KEY, mCurrentMenuItemType.ordinal() );
		super.onSaveInstanceState( outState );
	}

	@Override
	public void onAttach( Activity activity )
	{
		super.onAttach( activity );
		try {
			mListener = (OnSlideMenuItemClick) activity;
		} catch ( ClassCastException e ) {
			throw new ClassCastException( activity.toString()
					+ " must implement OnSlideMenuItemClick" );
		}
	}

	@Override
	public void onActivityCreated( Bundle savedInstanceState )
	{
		super.onActivityCreated( savedInstanceState );

		if ( savedInstanceState != null ) {
			mCurrentMenuItemType = MenuItemType.values()[savedInstanceState
					.getInt( ITEM_TYPE_KEY )];
		} else {
			mCurrentMenuItemType = MenuItemType.QURAN;
		}

		SlideMenuItems adapter = new SlideMenuItems( getActivity() );
		adapter.add( new SlideMenuItem( R.string.quran, 0, MenuItemType.QURAN ) );
		adapter.add( new SlideMenuItem( R.string.lessons, 0,
				MenuItemType.LESSONS ) );
		adapter.add( new SlideMenuItem( R.string.playing_list, 0,
				MenuItemType.PLAYING_LIST ) );

		ListView list = (ListView) getActivity().findViewById(
				R.id.slidemenu_list );
		list.setChoiceMode( ListView.CHOICE_MODE_SINGLE );
		list.setAdapter( adapter );
		list.setItemChecked( mCurrentMenuItemType.ordinal(), true );

		list.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick( AdapterView<?> parent, View view,
					int position, long id )
			{
				SlideMenuItem item = (SlideMenuItem) parent.getAdapter()
						.getItem( position );
				mListener.onSlideMenuItemClick( item );
				mCurrentMenuItemType = item.type;
			}
		} );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState )
	{
		return inflater.inflate( R.layout.slidemenu, null );
	}

	public class SlideMenuItem {
		public final MenuItemType	type;
		public String				text	= null;
		public Drawable				icon	= null;

		public SlideMenuItem(int textResource, int icon_resource,
				MenuItemType type) {
			this.text = getResources().getString( textResource );
			if ( icon_resource != 0 ) {
				this.icon = getResources().getDrawable( icon_resource );
			}
			this.type = type;
		}
	}

	public class SlideMenuItems extends ArrayAdapter<SlideMenuItem> {
		@Override
		public View getView( int position, View convertView, ViewGroup parent )
		{
			if ( convertView == null ) {
				convertView = LayoutInflater.from( getContext() ).inflate(
						R.layout.slidemenu_row, null );
			}
			CheckedTextView title = (CheckedTextView) convertView
					.findViewById( R.id.title );
			title.setText( getItem( position ).text );
			Drawable icon = getItem( position ).icon;
			if ( icon != null ) {
				title.setCompoundDrawables( icon, null, null, null );
			}

			return convertView;
		}

		public SlideMenuItems(Context context) {
			super( context, 0 );
		}

	}

}
