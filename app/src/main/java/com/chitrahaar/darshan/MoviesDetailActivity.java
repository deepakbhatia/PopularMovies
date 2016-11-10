package com.chitrahaar.darshan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MoviesDetailActivity extends AppCompatActivity implements TrailerReviewTask.TrailerInterface
{

    private Movies selected_movie;
    private MovieDetailFragment fragment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_detail);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        selected_movie = getIntent().getParcelableExtra(getResources().getString(R.string.movies_detail_view));

        if (savedInstanceState == null)
        {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.


            TrailerReviewTask trailerReviewTask = new TrailerReviewTask(this,this);

            trailerReviewTask.execute( selected_movie.getMovie_id());

            Bundle arguments = new Bundle();
            arguments.putParcelable(getResources().getString(R.string.movies_detail_view), selected_movie);

            fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }


    }


    @Override
    public void getTrailer(String video_id, boolean network_error, boolean parse_error) {

        String youtube_url = null;
        if(video_id!=null)
        {
            youtube_url = String .format(getString(R.string.youtube_watch_uri),video_id);
        }

        if(fragment!=null)
        {
            fragment.updateMovieDetail(youtube_url);

        }
        else{
            Bundle arguments = new Bundle();

            arguments.putParcelable(getResources().getString(R.string.movies_detail_view), selected_movie);
            arguments.putString("trailer_url",youtube_url);

            fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();


        }
        //
        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=DZgAjV7ugbA")));


    }


}
