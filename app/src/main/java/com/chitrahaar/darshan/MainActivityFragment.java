package com.chitrahaar.darshan;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
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

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements GettheMoviesTask.ReturnMovies, AdapterView.OnItemClickListener{


    private static String SELECTED_GRID_ITEM = null;
    private static String SELECTED_SPINNER_KEY = null;

    private GridView movies_gridview;

    private MoviesAdapter moviesAdapter;

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


    public interface MoviesListCallback{
        public void gotMoviesList(ArrayList<Movies> moviesList);

    }

    public MainActivityFragment() {
    }


    public interface MovieSelectionCallback{

        public void onMovieItemSelected(Movies movie);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root_view = inflater.inflate(R.layout.fragment_main, container, false);

        relativeLayout = (RelativeLayout)root_view. findViewById(R.id
                .content_main);
        movies_gridview = (GridView) root_view.findViewById(R.id.movies_list);


        empty_view = root_view.findViewById(R.id.movie_grid_empty);

        progressBar = (ProgressBar) root_view.findViewById(R.id.arProgressBar);
        //empty_view.setVisibility(View.GONE);
        //Display when there is no Data Available
        //movies_gridview.setEmptyView(empty_view);

        movies_gridview.setOnItemClickListener(this);


        setHasOptionsMenu(true);

        SELECTED_GRID_ITEM = getActivity().getString(R.string.current_grid_item);
        SELECTED_SPINNER_KEY = getActivity().getString(R.string.current_spinner_item);
        return root_view;
    }

    //Interface method to get callback from Task when list of movies is ready
    @Override
    public void returnMovies(ArrayList<Movies> moviesList, boolean network_error, boolean parse_error) {


        if(moviesList!=null)
        {
            returnedList = moviesList;
            empty_view.setVisibility(View.GONE);

            moviesAdapter = new MoviesAdapter(getActivity(),moviesList);
            movies_gridview.setAdapter(moviesAdapter);
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

        Movies selected_movie = moviesAdapter.getItem(position);

        ((MovieSelectionCallback)getActivity()).onMovieItemSelected(selected_movie);
    }

    //TODO Get Loader Manager in onActivityCreated
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        progressBar.setVisibility(View.GONE);


        empty_view.setVisibility(View.VISIBLE);
        if (savedInstanceState != null){

            if(savedInstanceState.containsKey(getString(R.string.showing_connect_message))){
                Boolean connect_message = savedInstanceState.getBoolean(getString(R.string.showing_connect_message));
                if(connect_message)
                {
                    notConnectedMessage();
                }
            }

            if(savedInstanceState.containsKey(getString(R.string.current_movie_selection))){
                returnedList = savedInstanceState.getParcelableArrayList("Movies");

                moviesAdapter = new MoviesAdapter(getActivity(), returnedList);
                movies_gridview.setAdapter(moviesAdapter);
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

            }
        }
        else
        {
                executeTasks(getActivity().getString(R.string.popular_tag));

        }

        super.onActivityCreated(savedInstanceState);

    }
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

        if(moviesAdapter!=null)
        outState.putParcelableArrayList(getString(R.string.current_movie_selection),returnedList);
        super.onSaveInstanceState(outState);
    }

    private void getMovies(){
        if (spinnerSelection == 0) {
            executeTasks(getActivity().getString(R.string.popular_tag));
        } else {
            executeTasks(getActivity().getString(R.string.top_rated_tag));
        }
    }
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
