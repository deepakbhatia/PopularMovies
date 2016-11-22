package com.chitrahaar.darshan;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.chitrahaar.darshan.data.MovieDataContract;
import com.chitrahaar.darshan.syncmovies.MovieSyncAdapter;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements GettheMoviesTask.ReturnMovies,

        AdapterView.OnItemClickListener,

        LoaderManager.LoaderCallbacks<Cursor>{


    private static String SELECTED_GRID_ITEM = null;
    private static String SELECTED_SPINNER_KEY = null;

    private GridView movies_gridview;

    private MoviesAdapter moviesAdapter;

    private MovieGridAdapter movieGridAdapter;

    private View empty_view;//View to Load when there is no internet connection

    private int mPosition = GridView.INVALID_POSITION;

    private  Spinner sort_spinner;

    private int keyVal = -1;

    private int spinnerSelection = 0;

    private  RelativeLayout relativeLayout;

    private View root_view;

    public static ProgressBar progressBar;

    private ArrayList<Movies> returnedList;

    private  Snackbar connectRefresh;

    private static final int MOVIES_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MovieDataContract.MovieDataEntry._ID,
            MovieDataContract.MovieDataEntry.COLUMN_MOVIE_TITLE,
            MovieDataContract.MovieDataEntry.COLUMN_RELEASEDATE,
            MovieDataContract.MovieDataEntry.COLUNM_PLOT,
            MovieDataContract.MovieDataEntry.COLUMN_RATING,
            MovieDataContract.MovieDataEntry.COLUMN_POSTER,
            MovieDataContract.MovieDataEntry.COLUMN_TYPE_LIST,
            MovieDataContract.MovieDataEntry.COLUMN_UPDATE_DATE,
            MovieDataContract.MovieDataEntry.COLUMN_IS_FAVOURITE,
            MovieDataContract.MovieDataEntry.COLUMN_ORIGINAL_LANGUAGE,
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
    static final int COL_ORIGINAL_LANGUAGE = 9;
    static final int COL_POSTER_BLOB = 10;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String listQuery = Utility.getSort_type();

        String destinatioUri = null;
        if(listQuery.equals(getActivity().getString(R.string.popular_tag))) {
            destinatioUri = "popular";
        }
        else  if(listQuery.equals(getActivity().getString(R.string.top_rated_tag))){
            destinatioUri = "top_rated";

        }else{
            destinatioUri = "favourite";
        }
        Uri baseMovieUri = MovieDataContract.MovieDataEntry.CONTENT_URI;
        Uri showMovieUri = Uri.parse(baseMovieUri.toString())
                .buildUpon()
                .appendPath(destinatioUri)
                .build();

        //TODO
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

        //TODO
        Log.d("movieGridAdapter",loader.toString());
        movieGridAdapter.swapCursor(data);

        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieGridAdapter.swapCursor(null);

    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    public interface MoviesListCallback{
        public void gotMoviesList(ArrayList<Movies> moviesList);

    }

    public MainActivityFragment() {
    }


    public interface MovieSelectionCallback{

        public void onMovieItemSelected(Movies movie);
    }

    @CallSuper
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        movieGridAdapter = new MovieGridAdapter(getActivity(), null,0);

        root_view = inflater.inflate(R.layout.fragment_main, container, false);

        relativeLayout = (RelativeLayout)root_view. findViewById(R.id
                .content_main);
        movies_gridview = (GridView) root_view.findViewById(R.id.movies_list);
        movies_gridview.setAdapter(movieGridAdapter);



        empty_view = root_view.findViewById(R.id.movie_grid_empty);

        progressBar = (ProgressBar) root_view.findViewById(R.id.arProgressBar);
        //empty_view.setVisibility(View.GONE);
        //Display when there is no Data Available
        movies_gridview.setEmptyView(empty_view);

        movies_gridview.setOnItemClickListener(this);



        Utility.setSort_type(getActivity().getString(R.string.popular_tag));

        setHasOptionsMenu(true);

        SELECTED_GRID_ITEM = getActivity().getString(R.string.current_grid_item);
        SELECTED_SPINNER_KEY = getActivity().getString(R.string.current_spinner_item);

        //progressBar.setVisibility(View.GONE);

        updateSort();

        return root_view;
    }

    //Interface method to get callback from Task when list of movies is ready
    @Override
    public void returnMovies(ArrayList<Movies> moviesList, boolean network_error, boolean parse_error) {


        if(moviesList!=null)
        {
            returnedList = moviesList;
            empty_view.setVisibility(View.GONE);

            //TODO previous version
            /*moviesAdapter = new MoviesAdapter(getActivity(),moviesList);
            movies_gridview.setAdapter(moviesAdapter);*/
            //Calls the interface in Mainactivity that checks if its tablet layout, to fill
            ((MoviesListCallback)getActivity()).gotMoviesList(moviesList);

        }else{

            if(network_error){
                empty_view.setVisibility(View.VISIBLE);
                notConnectedMessage();
            }

        }

    }

    private void notConnectedMessage()
    {
        connectRefresh =  Snackbar.make(relativeLayout,getString(R.string.not_connected_snackbar_message),Snackbar.LENGTH_INDEFINITE);

        connectRefresh.setAction(R.string.Dismiss, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectRefresh.dismiss();
            }
        }).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        if (cursor != null) {
            ((Callback) getActivity())
                    .onItemSelected(MovieDataContract.MovieDataEntry.createMovieDataPath(
                            cursor.getString(COL_MOVIE_ID)
                    ));
        }



        /*Movies selected_movie = moviesAdapter.getItem(position);

        ((MovieSelectionCallback)getActivity()).onMovieItemSelected(selected_movie);*/
    }

    @CallSuper
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        getLoaderManager().initLoader(MOVIES_LOADER, null, this);

        //TODO previous veriosn
       /* empty_view.setVisibility(View.VISIBLE);*/
        if (savedInstanceState != null){

            if(savedInstanceState.containsKey(getString(R.string.showing_connect_message))){
                Boolean connect_message = savedInstanceState.getBoolean(getString(R.string.showing_connect_message));
                if(connect_message)
                {
                    notConnectedMessage();
                }
            }

            if(savedInstanceState.containsKey(getString(R.string.current_movie_selection))){
                //TODO Previous Version
                //returnedList = savedInstanceState.getParcelableArrayList(getString(R.string.current_movie_selection));

                //moviesAdapter = new MoviesAdapter(getActivity(), returnedList);
                //movies_gridview.setAdapter(moviesAdapter);
                empty_view.setVisibility(View.GONE);

                //((MoviesListCallback)getActivity()).gotMoviesList(moviesList);
            }

            if (savedInstanceState.containsKey(SELECTED_GRID_ITEM)) {
                // The listview probably hasn't even been populated yet.  Actually perform the
                // swapout in onLoadFinished.
                mPosition = savedInstanceState.getInt(SELECTED_GRID_ITEM);
                movies_gridview.setSelection(mPosition);
                movies_gridview.smoothScrollToPosition(mPosition);
                movies_gridview.setItemChecked(mPosition,true);
            }
            if (savedInstanceState.containsKey(SELECTED_SPINNER_KEY)) {

                keyVal = savedInstanceState.getInt(SELECTED_SPINNER_KEY);
                spinnerSelection = savedInstanceState.getInt(SELECTED_SPINNER_KEY);
                updateSort();

            }
        }
        else
        {
                //executeTasks(getActivity().getString(R.string.popular_tag));

        }

        super.onActivityCreated(savedInstanceState);

    }

    @CallSuper
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.

        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_GRID_ITEM, mPosition);

        }
        outState.putInt(SELECTED_SPINNER_KEY ,spinnerSelection);

        outState.putBoolean(getString(R.string.showing_connect_message),!Utility.isNetworkAvailable(getActivity()));

        //TODO Previous Version
       /* if(moviesAdapter!=null)
        outState.putParcelableArrayList(getString(R.string.current_movie_selection),returnedList);*/
        super.onSaveInstanceState(outState);
    }

    private void updateSort(){
        if (spinnerSelection == 0) {
            Utility.setSort_type(getActivity().getString(R.string.popular_tag));
            //executeTasks(getActivity().getString(R.string.popular_tag));
        } else if (spinnerSelection == 1){
            Utility.setSort_type(getActivity().getString(R.string.top_rated_tag));

            //executeTasks(getActivity().getString(R.string.top_rated_tag));
        }else if (spinnerSelection == 2){
            Utility.setSort_type(getActivity().getString(R.string.favourite));
            ((TextView)empty_view).setText(R.string.no_favourites_message);
            ((TextView)empty_view).setContentDescription(getString(R.string.no_favourites_message));
            ((TextView)empty_view).setCompoundDrawablesWithIntrinsicBounds(null,null,null,getActivity().getResources().getDrawable(R.mipmap.ic_no_favourites));

            //executeTasks(getActivity().getString(R.string.top_rated_tag));
        }

    }
    private void getMovies(){

        ((TextView)empty_view).setText(R.string.no_connection_message);
        ((TextView)empty_view).setContentDescription(getString(R.string.no_connection_message));
        ((TextView)empty_view).setCompoundDrawablesWithIntrinsicBounds(null,null,null,getActivity().getResources().getDrawable(R.mipmap.ic_wifi));

        if (spinnerSelection == 0) {
            Utility.setSort_type(getActivity().getString(R.string.popular_tag));
            //TODO
            Log.d("getMovies","getMovies"+spinnerSelection);

            //executeTasks(getActivity().getString(R.string.popular_tag));
        } else if (spinnerSelection == 1){
            Utility.setSort_type(getActivity().getString(R.string.top_rated_tag));

            //TODO
            Log.d("getMovies","getMovies"+spinnerSelection);


            //executeTasks(getActivity().getString(R.string.top_rated_tag));


        }else if (spinnerSelection == 2){
            Utility.setSort_type(getActivity().getString(R.string.favourite));
            ((TextView)empty_view).setText(R.string.no_favourites_message);
            ((TextView)empty_view).setContentDescription(getString(R.string.no_favourites_message));
            ((TextView)empty_view).setCompoundDrawablesWithIntrinsicBounds(null,null,null,getActivity().getResources().getDrawable(R.mipmap.ic_no_favourites));

            //executeTasks(getActivity().getString(R.string.top_rated_tag));
        }

        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);

        MovieSyncAdapter.syncImmediately(getActivity());

    }

    @CallSuper
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        //Sort Menu
        MenuItem sort_item = menu.findItem(R.id.spinner);
        sort_spinner = (Spinner) MenuItemCompat.getActionView(sort_item);

        sort_spinner.setPopupBackgroundResource(R.color.colorPrimary);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sort_elements, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        sort_spinner.setAdapter(adapter);

        sort_spinner.setContentDescription(getString(R.string.sort_menu_content_description));

        sort_spinner.setSelection(spinnerSelection);

        sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //Check if spinner item triggered by selection or by Rotation or Pause & Resume.
                //keyVal = -1, for selection events or set to index of previous selection when
                //rotation takes place or activity is resumed.
                if(keyVal == -1) {

                    spinnerSelection = position; //Set position of the selected spinner item.
                    getMovies();

                }
                else {
                    keyVal = -1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Refresh Item in Menu
        MenuItem refresh_item = menu.findItem(R.id.referesh);

        refresh_item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getMovies();
                return true;
            }
        });

    }

    private void executeTasks(String sort)
    {
            if(Utility.isNetworkAvailable(getActivity()))
            {
                progressBar.setVisibility(View.VISIBLE);

                GettheMoviesTask gettheMoviesTask = new GettheMoviesTask(getActivity(),this);

                gettheMoviesTask.execute(sort);
                //Dismiss snackbar message shown on no internet connection, if its visible.
                if(connectRefresh!=null && connectRefresh.isShown())
                {
                    connectRefresh.dismiss();
                }
            }
            else {
                if(!(returnedList!=null && returnedList.size()>0))
                {
                    empty_view.setVisibility(View.VISIBLE);
                }
                notConnectedMessage();
            }




    }

}
