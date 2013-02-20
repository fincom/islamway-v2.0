package com.symbyo.islamway;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.ViewGroup;
import com.symbyo.islamway.fragments.SlideMenuFragment;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * @author kdehairy
 * @since 2/20/13
 */
public class Utils {

    public static Style CROUTON_PROGRESS_STYLE = new Style.Builder().setDuration(
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
            if ( network_type != ConnectivityManager.TYPE_WIFI && wifi_only ) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isWifiOnly( Context context )
    {
        SharedPreferences prefs = context.getSharedPreferences(
                SlideMenuFragment.PREFS_FILE, 0 );
        return prefs.getBoolean( SlideMenuFragment.PREFS_WIFIONLY, true );
    }
}
