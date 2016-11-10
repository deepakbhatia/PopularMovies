package com.chitrahaar.darshan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.MoviesListCallback, MainActivityFragment.MovieSelectionCallback, TrailerReviewTask.TrailerInterface {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment(), getString(R.string.movie_detail_fragment_tag))
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);

        }

        MainActivityFragment mainActivityFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.movies_list_fragment_tag));

        if(mainActivityFragment == null)
            mainActivityFragment = new MainActivityFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_movies,mainActivityFragment,getString(R.string.movies_list_fragment_tag))
                .commit();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }


    private void getMovieTrailer(Movies selected_movie)
    {
        TrailerReviewTask trailerReviewTask = new TrailerReviewTask(this,this);

        trailerReviewTask.execute( selected_movie.getMovie_id());

    }
    @Override
    public void onMovieItemSelected(Movies selected_movie) {

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            getMovieTrailer(selected_movie);

            twoPaneDetailView(selected_movie);



        }else {
            //Phone Layout, Open the Detail View Activity
            Intent intent = new Intent(this,MoviesDetailActivity.class);

            intent.putExtra(getResources().getString(R.string.movies_detail_view),selected_movie);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }

    private MovieDetailFragment fragment;
    private void twoPaneDetailView(Movies selected_movie)
    {
        Bundle args = new Bundle();
        args.putParcelable(getResources().getString(R.string.movies_detail_view), selected_movie);

        fragment = new MovieDetailFragment();
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_detail_container, fragment, getString(R.string.movie_detail_fragment_tag))
                .commit();
    }

    @Override
    public void gotMoviesList(ArrayList<Movies> moviesList) {
        //
        if (mTwoPane) {
            Movies selected_movie = moviesList.get(0);
            getMovieTrailer(selected_movie);
            twoPaneDetailView(selected_movie);


        }
    }

    @Override
    public void getTrailer(String video_id, boolean network_error, boolean parse_error) {
        String youtube_url = null;

        if (mTwoPane) {

            if(video_id!=null)
            {
                youtube_url = String .format(getString(R.string.youtube_watch_uri),video_id);
            }

            if(fragment!=null)
            {
                fragment.updateMovieDetail(youtube_url);

            }
            /*else{
                Bundle arguments = new Bundle();

                arguments.putParcelable(getResources().getString(R.string.movies_detail_view), selected_movie);
                arguments.putString("trailer_url",youtube_url);

                fragment = new MovieDetailFragment();
                fragment.setArguments(arguments);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_detail_container, fragment)
                        .commit();


            }*/
        }

    }
}
