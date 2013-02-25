package com.symbyo.islamway;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import com.symbyo.islamway.domain.FilterableObject;
import com.symbyo.islamway.fragments.SlideMenuFragment;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kdehairy
 * @since 2/20/13
 */
public class Utils {

    public static final Style CROUTON_PROGRESS_STYLE = new Style.Builder()
            .setDuration(
                    Style.DURATION_INFINITE )
            .setBackgroundColorValue( Style.holoBlueLight )
            .setHeight( ViewGroup.LayoutParams.WRAP_CONTENT )
            .build();

    public static boolean isNetworkAvailable( Context context )
    {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if ( networkInfo != null && networkInfo.isConnected() ) {
            int network_type = networkInfo.getType();
            boolean wifi_only = isWifiOnly( context );
            return !(network_type != ConnectivityManager.TYPE_WIFI && wifi_only);
        }
        return false;
    }

    public static boolean isWifiOnly( Context context )
    {
        SharedPreferences prefs = context.getSharedPreferences(
                SlideMenuFragment.PREFS_FILE, 0 );
        return prefs.getBoolean( SlideMenuFragment.PREFS_WIFIONLY, true );
    }

    public static class ArrayFilter<T extends FilterableObject> extends Filter {
        private List<T>     mOriginalData;
        private List<T>     mData;
        private BaseAdapter mAdapter;

        public ArrayFilter( BaseAdapter adapter, List<T> data )
        {
            super();
            mOriginalData = new ArrayList<T>( data );
            mData = data;
            mAdapter = adapter;
        }

        @Override
        protected FilterResults performFiltering( CharSequence prefix )
        {
            FilterResults results = new FilterResults();
            if ( prefix == null || prefix.length() == 0 ) {
                List<T> list = new ArrayList<T>( mOriginalData );
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString();

                List<T> values = new ArrayList<T>( mOriginalData );

                //final int count = values.size();
                final List<T> newValues = new ArrayList<T>();

                for ( final T item : values ) {
                    final String valueText = item.getTitle();

                    // First match against the whole, non-splitted value
                    if ( valueText.startsWith( prefixString ) ) {
                        newValues.add( item );
                    } else {
                        final String[] words = valueText.split( " " );
                        //final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with
                        // space(s)
                        for ( String word : words ) {
                            if ( word.startsWith( prefixString ) ) {
                                newValues.add( item );
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(
                CharSequence constraint, FilterResults results )
        {
            mData.clear();
            mData.addAll( (List<T>) results.values );
            if ( results.count > 0 ) {
                mAdapter.notifyDataSetChanged();
            } else {
                mAdapter.notifyDataSetInvalidated();
            }
        }
    }
}
