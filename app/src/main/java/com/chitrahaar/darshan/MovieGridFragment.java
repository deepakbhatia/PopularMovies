package com.chitrahaar.darshan;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.chitrahaar.darshan.data.MovieDataContract;
import com.chitrahaar.darshan.syncmovies.MovieSyncAdapter;

/**
 * Created by obelix on 22/11/2016.
 */

public class MovieGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    private GridView movies_gridview;


    private MovieGridAdapter movieGridAdapter;

    private static final int MOVIES_LOADER = 0;

    private  RelativeLayout relativeLayout;

    private View root_view;

    private int spinnerSelection = 0;


    private static final String[] MOVIE_COLUMNS = {
            MovieDataContract.MovieDataEntry._ID,
            MovieDataContract.MovieDataEntry.COLUMN_MOVIE_TITLE,
            MovieDataContract.MovieDataEntry.COLUMN_RELEASEDATE,
            MovieDataContract.MovieDataEntry.COLUNM_PLOT,
            MovieDataContract.MovieDataEntry.COLUMN_RATING,
            MovieDataContract.MovieDataEntry.COLUMN_POSTER,
            MovieDataContract.MovieDataEntry.COLUMN_TYPE_LIST,
            //MovieDataContract.MovieDataEntry.COLUMN_UPDATE_DATE,
            MovieDataContract.MovieDataEntry.COLUMN_IS_FAVOURITE,
            MovieDataContract.MovieDataEntry.COLUMN_POSTER_BLOB

    };

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_RELEASEDATE = 2;
    static final int COL_DESCRIPTION = 3;
    static final int COL_RATING = 4;
    public static final int COLUMN_POSTER = 5;
    static final int COL_TYPE_LIST = 6;
    static final int COL_UPDATE_DATE = 7;
    static final int COL_IS_FAVOURITE = 8;
    static final int COL_POSTER_BLOB = 9;

    public MovieGridFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        movieGridAdapter = new MovieGridAdapter(getActivity(), null, 0);

        root_view = inflater.inflate(R.layout.fragment_main, container, false);

        relativeLayout = (RelativeLayout) root_view.findViewById(R.id
                .content_main);
        movies_gridview = (GridView) root_view.findViewById(R.id.movies_list);

        updateSort();


        return root_view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        getLoaderManager().initLoader(MOVIES_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    private void updateSort(){
        if (spinnerSelection == 0) {
            Utility.setSort_type(getActivity().getString(R.string.popular_tag));
            //executeTasks(getActivity().getString(R.string.popular_tag));
        } else if (spinnerSelection == 1){
            Utility.setSort_type(getActivity().getString(R.string.top_rated_tag));

            //executeTasks(getActivity().getString(R.string.top_rated_tag));
        }

        MovieSyncAdapter.syncImmediately(getActivity());


    }

        @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String listQuery = Utility.getSort_type();

        Uri baseMovieUri = MovieDataContract.MovieDataEntry.CONTENT_URI;
        Uri showMovieUri = Uri.parse(baseMovieUri.toString())
                .buildUpon()
                .build();

            Log.d("showMovieUri",showMovieUri.toString());

        return new CursorLoader(getActivity(),
                showMovieUri,
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        movieGridAdapter.swapCursor(data);

        Log.d("movieGridAdapter",""+data.isAfterLast());

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieGridAdapter.swapCursor(null);
    }
}
