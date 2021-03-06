package com.symbyo.islamway.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.symbyo.islamway.R;
import com.symbyo.islamway.Utils;
import com.symbyo.islamway.domain.Entry;

import java.util.List;

/**
 * @author kdehairy
 * @since 2/20/13
 */
public class EntryAdapter extends BaseAdapter
		implements Filterable {

	private List<Entry> mEntries;
	private Context     mContext;

	private final int TYPE_COLLECTION = 0;
	private final int TYPE_LEAF       = 1;


	/**
	 * when filtered, this holds the original list while mScholars will hold
	 * only the filtered list.
	 */
	private Utils.ArrayFilter<Entry> mFilter;

	private final Object mLock = new Object();

	private final int ITEM_LAYOUT = R.layout.collection_list_item;

	public EntryAdapter( Context context, List<Entry> entries )
	{
		mEntries = entries;
		mContext = context;
	}

	/**
	 * How many items are in the data set represented by this Adapter.
	 *
	 * @return Count of items.
	 */
	@Override
	public int getCount()
	{
		int size = 0;
		if ( mEntries != null ) {
			size = mEntries.size();
		}
		return size;
	}

	/**
	 * Get the data item associated with the specified position in the data set.
	 *
	 * @param position Position of the item whose data we want within the adapter's
	 *                 data set.
	 * @return The data at the specified position.
	 */
	@Override
	public Entry getItem( int position )
	{
		Entry entry = null;
		if ( mEntries != null ) {
			entry = mEntries.get( position );
		}
		return entry;
	}

	@Override
	public int getItemViewType( int position )
	{
		Entry entry = getItem( position );
		int type;
		switch ( entry.getType() ) {
			case GROUP:
			case LESSONS_SERIES:
			case MUSHAF:
				type = TYPE_COLLECTION;
				break;
			case LESSON:
			case QURAN_RECITATION:
				type = TYPE_LEAF;
				break;
			default:
				type = TYPE_LEAF;
		}
		return type;
	}

	@Override
	public int getViewTypeCount()
	{
		return 2;
	}

	/**
	 * Get the row id associated with the specified position in the list.
	 *
	 * @param position The position of the item within the adapter's data set whose row id we want.
	 * @return The id of the item at the specified position.
	 */
	@Override
	public long getItemId( int position )
	{
		return position;
	}

	/**
	 * Get a View that displays the data at the specified position in the data set. You can either
	 * create a View manually or inflate it from an XML layout file. When the View is inflated, the
	 * parent View (GridView, ListView...) will apply default layout parameters unless you use
	 * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
	 * to specify a root view and to prevent attachment to the root.
	 *
	 * @param position    The position of the item within the adapter's data set of the item whose view
	 *                    we want.
	 * @param convertView The old view to reuse, if possible. Note: You should check that this view
	 *                    is non-null and of an appropriate type before using. If it is not possible to convert
	 *                    this view to display the correct data, this method can create a new view.
	 *                    Heterogeneous lists can specify their number of view types, so that this View is
	 *                    always of the right type (see {@link #getViewTypeCount()} and
	 *                    {@link #getItemViewType(int)}).
	 * @param parent      The parent that this view will eventually be attached to
	 * @return A View corresponding to the data at the specified position.
	 */
	@Override
	public View getView(
			int position, View convertView, ViewGroup parent )
	{
		if ( convertView == null ) {
			convertView = LayoutInflater.from( mContext ).inflate( ITEM_LAYOUT,
																   null );
		}
		ViewHolder holder;
		if ( convertView.getTag() == null
				|| !(convertView.getTag() instanceof ViewHolder) ) {
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(
					R.id.collection_title );
			holder.subTitle = (TextView) convertView.findViewById(
					R.id.entry_count );
			convertView.setTag( holder );
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		switch ( getItemViewType( position ) ) {
			case TYPE_COLLECTION: {
				Entry entry = getItem( position );
				holder.title.setText( entry.getTitle() );
				int entity_count = entry.getEntriesCount();
				holder.subTitle.setText( Integer.toString( entity_count ) );
				break;
			}
			case TYPE_LEAF: {
				Entry entry = getItem( position );
				holder.title.setText( entry.getTitle() );
			}
		}

		/*String string;
		if ( entity_count == 1 ) {
            string = String.format( Locale.US, "%s", mContext.getString( R.string.one_sura ) );
        } else if ( entity_count == 2 ) {
            string = String.format( Locale.US, "%s", mContext.getString( R.string.two_suras ) );
        } else if ( entity_count < 11 ) {
            string = String.format( Locale.US, "%d %s", entity_count, mContext.getString( R.string.suras ) );
        } else {
            string = String.format( Locale.US, "%d %s", entity_count, mContext.getString( R.string.sura ) );
        }*/
		return convertView;
	}

	private static class ViewHolder {
		public TextView title;
		public TextView subTitle;
	}


	/**
	 * <p>Returns a filter that can be used to constrain data with a filtering
	 * pattern.</p>
	 * <p/>
	 * <p>This method is usually implemented by {@link android.widget.Adapter}
	 * classes.</p>
	 *
	 * @return a filter used to constrain data
	 */
	@Override
	public Filter getFilter()
	{
		if ( mFilter == null ) {
			mFilter = new Utils.ArrayFilter<Entry>( this, mEntries );
		}
		return mFilter;
	}
}
