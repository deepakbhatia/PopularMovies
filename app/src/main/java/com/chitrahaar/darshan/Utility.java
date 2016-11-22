package com.chitrahaar.darshan;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by obelix on 27/10/2016.
 */

public class Utility {

    public static final int MOVIE_TAG = 1000;
    public static String sort_type = null;
    public static int POPULAR_LIST = 1;
    public static int TOP_RATED_LIST = 2;
    public static int BOTH_LIST = 3;
    public static int FAVOURITE_LIST = 4;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getSort_type()
    {
        return sort_type;
    }

    public static void setSort_type(String sort_value)
    {
        sort_type = sort_value;
    }
}
