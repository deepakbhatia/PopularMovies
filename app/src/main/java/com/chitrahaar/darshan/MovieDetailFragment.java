package com.chitrahaar.darshan;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chitrahaar.darshan.data.MovieDataContract;

import java.util.ArrayList;

/**
 * Created by obelix on 29/10/2016.
 */

public class MovieDetailFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private ImageView movie_image;
    private TextView movie_title;
    private TextView movie_overview;
    private TextView movie_rating;

    private CardView movie_card_view;
    private TextView movie_release_date;
    private ArrayList<Trailer> movie_trailer_url_list;
    private Button movie_trailer;

    private Movies movies;
    private Resources res;
    private ArrayList<Reviews> reviews;

    private RelativeLayout rootlayout;

    private ListView reviewsListView;

    private ReviewsAdapter reviewsAdapter;

    private String movie_title_text;
    private RecyclerView movieDetailView;
    private MoviesViewAdapter movieContentAdapter;

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

    private static final int MOVIE_DETAIL_LOADER = 1000;
    private Uri mUri;

    @Override
    public void onClick(View v) {

        int view_id = v.getId();

        switch (view_id){
            case R.id.play_trailer:
                if(movie_trailer_url_list!=null)
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse( String .format(getString(R.string.youtube_watch_uri),movie_trailer_url_list.get(0).getmTrailerKey()))));
                else{
                    Snackbar.make(rootlayout, R.string.cannot_find_trailer,Snackbar.LENGTH_INDEFINITE).

                    setAction(R.string.Dismiss, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
                }
                break;
        }

    }

    public void updateMovieDetail(@Nullable  ArrayList<Object> youtube_url)
    {
        movieContentAdapter.addObjects(youtube_url);
    }

    public void updateMovieReviews(ArrayList<Reviews> reviews)
    {
        this.reviews = reviews;

        //TODO
        Log.d("reviewsAdapter",""+this.reviews.size());
        reviewsAdapter = new ReviewsAdapter(getActivity(),this.reviews,movie_title_text);

        reviewsListView.setAdapter(reviewsAdapter);

        if(reviewsAdapter!=null)
            reviewsAdapter.notifyDataSetChanged();

    }
    //Zero Argument Constructor
    public MovieDetailFragment()
    {

    }

    @CallSuper
    @Override
    public @NonNull View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View root_view = inflater.inflate(R.layout.content_movies_detail,container,false);

        res = getResources();

        rootlayout = (RelativeLayout)root_view.findViewById(R.id.target_layout);
        //movie_card_view = (CardView)root_view.findViewById(R.id.movie_card_view);
        //Get the detail view movie passed in the Bundle to set to the view

        //movie_trailer.setOnClickListener(this);

        movieDetailView = (RecyclerView) root_view.findViewById(R.id.movie_detail_view);


        //reviewsListView = (ListView)root_view.findViewById(R.id.review_list);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        movieDetailView.setLayoutManager(mLinearLayoutManager);

        movieContentAdapter = new MoviesViewAdapter(getContext());
        movieDetailView.setAdapter(movieContentAdapter);

        //movieDetailView.addOnItemTouchListener(this);
        Bundle arguments = getArguments();
        if(arguments != null)
        {

            mUri = arguments.getParcelable(getResources().getString(R.string.movies_detail_view));

            //TODO Previous Version
            /*movies = arguments.getParcelable(res.getString(R.string.movies_detail_view));

            ArrayList<Object> moviesobject = new ArrayList<>();

            moviesobject.add(movies);

            movieContentAdapter.addObjects(moviesobject);
*/

            /*if(arguments.containsKey("trailer_url"))
            {
                movie_trailer_url_list = arguments.getParcelableArrayList("trailer_url");
            }
            movie_title_text = movies.getMovies_title();
            String movie_rating_text = String.format(res.getString(R.string.movie_rating_text),String.valueOf(movies.getMovies_rating()));


            movie_title.setText(movie_title_text);
            movie_title.setContentDescription(String.format(res.getString(R.string.movie_detail_title_content_description),movie_title_text));


            String movie_overview_text = movies.getMovies_overview();
            movie_overview.setText(movie_overview_text);
            movie_overview.setContentDescription(String.format(res.getString(R.string.movie_detail_overview_content_description),movie_title_text,movie_overview_text));


            movie_rating.setText(movie_rating_text);
            movie_rating.setContentDescription(movie_rating_text);


            String movie_release = movies.getRelease_date();


            movie_release_date.setText(String.format(getString(R.string.movie_release), movie_release));
            movie_release_date.setContentDescription(movie_release);


            movie_trailer.setContentDescription(String.format(getString(R.string.play_movie_trailer_content_description),movie_title_text));
            Picasso.with(getActivity())
                    .load(getString(R.string.movie_db_poster_base_url)+getString(R.string.movies_db_poster_format)+movies.getMovies_image_url())
                    .error(R.mipmap.ic_wifi)
                    .into(movie_image);
            movie_image.setContentDescription(String.format(res.getString(R.string.movie_detail_image_content_description),movie_title_text));

*/


        }else{
            //movie_card_view.setContentDescription(getString(R.string.no_internet_movie_detail));
            //movie_title.setText(getString(R.string.no_internet_movie_detail));
        }



        return root_view;
    }

    @CallSuper
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @CallSuper
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        super.onSaveInstanceState(outState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {
            // initiatie a boolean to check if the adapter already contains a movie
            boolean hasMovie = false;

            //loop through the adapter and check if we have a movie in case there is one. Set hasMovie to true
            for (int i = 0; i < movieContentAdapter.getItemCount(); i++) {
                if (movieContentAdapter.getObject(i) instanceof Movies) {
                    hasMovie = true;
                }
            }
            //if there is already a movie do nothing else add the movie to the adapter
            if (hasMovie == true) {

            } else {
                String movieId = data.getString(COL_MOVIE_ID);
                Movies mMovie = new Movies(
                        data.getString(COL_MOVIE_TITLE),
                        data.getString(COLUMN_POSTER),
                        data.getString(COL_DESCRIPTION),
                        data.getDouble(COL_RATING),
                        data.getString(COL_ORIGINAL_LANGUAGE),
                        data.getString(COL_RELEASEDATE)
                        );

               mMovie.setMovie_id(movieId);
               mMovie.setMovie_list( data.getInt(COL_TYPE_LIST));
               mMovie.setFavourite(         data.getString(COL_IS_FAVOURITE));

                ArrayList<Object> mMovies = new ArrayList<>();
                mMovies.add(mMovie);

               /* if (data.getString(COL_FAVOURITE).equals("YES")) {
                    mIsFavourite = true;
                } else {
                    mIsFavourite = false;
                }*/

                movieContentAdapter.addObjects(mMovies);

                TrailersTask trailerReviewTask = new TrailersTask(getActivity(),movieContentAdapter);

                trailerReviewTask.execute(movieId);

                MovieReviewTask movieReviewTask = new MovieReviewTask(getActivity(),movieContentAdapter);

                movieReviewTask.execute(movieId);
            }

        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
