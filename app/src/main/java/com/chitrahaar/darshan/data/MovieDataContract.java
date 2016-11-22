package com.chitrahaar.darshan.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by obelix on 21/11/2016.
 */

public class MovieDataContract {

    //define unique content authority
    public static final String CONTENT_AUTHORITY = "com.chitrahaar.darshan";

    // defining base uri and use the content authority
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //defining paths for different data
    public static final String PATH_MOVIE = "movies";

    public static final String PATH_POPULAR_MOVIE = "popular";

    public static final String PATH_TOP_RATED_MOVIE = "top_rated";

    public static final String PATH_FAV_MOVIE = "favourite";

    // this class will contain the details of the movie table
    public static final class MovieDataEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final Uri CONTENT_POPULAR_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).appendPath(PATH_POPULAR_MOVIE).build();

        public static final Uri CONTENT_TOP_RATED_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).appendPath(PATH_TOP_RATED_MOVIE).build();
        //defining values for the mime types
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        //table name
        public static final String TABLE_NAME = "movie";

        //defining the fields of the table

        //title for of the movie stored as string
        public static final String COLUMN_MOVIE_TITLE = "movie_title";

        //release date stored as varchar
        public static final String COLUMN_RELEASEDATE = "movie_release_date";

        //plot synopsis provided by the api
        public static final String COLUNM_PLOT = "movie_plot";

        //average rating of the movie
        public static final String COLUMN_RATING = "movie_rating";

        //url of the movie poster name
        public static final String COLUMN_POSTER = "movie_poster";

        //url for the movie category
        public static final String COLUMN_TYPE_LIST = "movie_sort_list";

        //url for flagging whether or not the movie still exists in the json fetch of a specific list
        public static final String COLUMN_UPDATE_DATE = "movie_update";

        public static final String COLUMN_IS_FAVOURITE = "movie_favourite";

        public static final String COLUMN_POSTER_BLOB = "movie_image";

        public static final String COLUMN_ORIGINAL_LANGUAGE = "movie_original_language";


        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri createMovieDataPath(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }
    }
}
