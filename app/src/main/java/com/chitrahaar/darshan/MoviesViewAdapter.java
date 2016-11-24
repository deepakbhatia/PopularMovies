package com.chitrahaar.darshan;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chitrahaar.darshan.data.MovieDataContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by obelix on 11/11/2016.
 */

public class MoviesViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_COUNT = 3;
    private static final int VIEW_TYPE_DETAIL = 0;
    private static final int VIEW_TYPE_TRAILERS = 1;
    private static final int VIEW_TYPE_REVIEWS = 2;
    private static final int VIEW_TYPE_HEADERS = 5;
    private static final String LOG_TAG = MoviesViewAdapter.class.getName();


    // The items to display in your RecyclerView
    private ArrayList<Object> moviesList = new ArrayList<>();

    private Context mContext;

    private ContentValues movieValues;
    private static String movieTitle;

    public MoviesViewAdapter(Context context) {
        this.mContext = context;

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater view_inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_DETAIL: {
                View movie_view = view_inflater.inflate(R.layout.movies_detail_item, parent, false);
                viewHolder = new MovieViewHolder(movie_view);
                break;
            }

            case VIEW_TYPE_TRAILERS: {
                View trailer_view = view_inflater.inflate(R.layout.trailer_item, parent, false);
                viewHolder = new TrailerViewHolder(trailer_view);
                break;
            }
            case VIEW_TYPE_REVIEWS: {

                View review_view = view_inflater.inflate(R.layout.reviews_item, parent, false);

                viewHolder = new ReviewViewHolder(review_view);
                break;
            }
            case VIEW_TYPE_HEADERS: {

                View header_view = view_inflater.inflate(R.layout.movies_detail_header_item, parent, false);

                viewHolder = new HeaderViewHolder(header_view);
                break;
            }
        }
        return viewHolder;
    }


    @Override
    public int getItemViewType(int position) {
        if (moviesList.get(position) instanceof Trailer) {
            return VIEW_TYPE_TRAILERS;
        } else if (moviesList.get(position) instanceof Reviews) {
            return VIEW_TYPE_REVIEWS;
        } else if (moviesList.get(position) instanceof Movies) {
            return VIEW_TYPE_DETAIL;
        }else if (moviesList.get(position) instanceof ViewHeaders) {
            return VIEW_TYPE_HEADERS;
        }

        return -1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        int viewType = holder.getItemViewType();
        switch (viewType) {
            case VIEW_TYPE_DETAIL: {
                // Get weather icon
                MovieViewHolder viewHolderMovie = (MovieViewHolder) holder;
                setMovieViewHolder(viewHolderMovie, position);
                break;
            }

            case VIEW_TYPE_TRAILERS: {
                // Get weather icon
                TrailerViewHolder trailerViewHolder = (TrailerViewHolder) holder;
                setTrailerView(trailerViewHolder, position);
                break;
            }
            case VIEW_TYPE_REVIEWS: {
                ReviewViewHolder reviewViewHolder = (ReviewViewHolder) holder;
                setReviewView(reviewViewHolder, position);
                break;
            }
            case VIEW_TYPE_HEADERS: {
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                setHeaderView(headerViewHolder, position);
                break;
            }
        }
    }


    /*

           HEADER VIEW HOLDER
    */

    private void setHeaderView(HeaderViewHolder headerViewHolder, int position) {
        ViewHeaders header = (ViewHeaders) moviesList.get(position);
        if (header != null) {
            headerViewHolder.getHeaderTitle().setText(header.getHeader_title());
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitle;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            headerTitle = (TextView) itemView.findViewById(R.id.header_label);
        }

        public TextView getHeaderTitle()
        {
            return headerTitle;
        }
    }

    /*

           REVIEW VIEW HOLDER
    */

    private void setReviewView(ReviewViewHolder reviewViewHolder, int position) {
        Reviews review = (Reviews) moviesList.get(position);
        if (review != null) {
            if(review.getAuthor()!=null)
            {
                reviewViewHolder.getReviewAuthorView().setText(review.getAuthor());
                reviewViewHolder.getReviewContentView().setText(review.getReview());

                reviewViewHolder.getReviewAuthorView().setContentDescription(String.format(mContext.getString(R.string.review_author_content_description),movieTitle,review.getAuthor()));

                reviewViewHolder.getReviewContentView().setContentDescription(String.format(mContext.getString(R.string.review_text_content_description),movieTitle,review.getReview()));

            }
            else{
                reviewViewHolder.getNoReviewContentView().setVisibility(View.VISIBLE);
                reviewViewHolder.getReviewCard().setVisibility(View.GONE);
                reviewViewHolder.getReviewAuthorView().setVisibility(View.GONE);
                reviewViewHolder.getReviewContentView().setVisibility(View.GONE);
            }

        }
    }
    //view holder for reviews
    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView reviewAuthor;
        TextView reviewContent;

        TextView noReviewView;

        CardView reviewCard;

        public ReviewViewHolder(View v) {
            super(v);
            reviewAuthor = (TextView) v.findViewById(R.id.review_author);
            reviewContent = (TextView) v.findViewById(R.id.review_content);
            noReviewView = (TextView) v.findViewById(R.id.no_reviews_message);
            reviewCard = (CardView)v.findViewById(R.id.review_card);

        }

        public CardView getReviewCard()
        {
            return reviewCard;
        }

        public TextView getReviewAuthorView() {
            return reviewAuthor;
        }

        public void setReviewAuthorView(TextView author) {
            this.reviewAuthor = author;
        }

        public TextView getReviewContentView() {
            return reviewContent;
        }

        public TextView getNoReviewContentView() {
            return noReviewView;
        }

        public void setReviewContentView(TextView content) {
            this.reviewContent = content;
        }

    }

    /*

            TRAILER VIEW HOLDER
     */


        private void setTrailerView(TrailerViewHolder trailerViewHolder, int position) {
            Trailer trailer = (Trailer) moviesList.get(position);
            if (trailer != null) {
                if (trailer.getmTrailerName() != null) {
                    trailerViewHolder.getTrailerPlay().setText(trailer.getmTrailerName());
                    trailerViewHolder.getTrailerPlay().setTag(trailer.getmTrailerKey());
                    trailerViewHolder.shareTrailerView.setTag(trailer.getmTrailerKey());
                } else {

                    trailerViewHolder.getNo_trailer_view().setVisibility(View.VISIBLE);
                    trailerViewHolder.getTrailerPlay().setVisibility(View.GONE);
                    trailerViewHolder.shareTrailerView.setVisibility(View.GONE);
                }
            }
        }
    //view holder for trailers
    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Button trailer_play;
        private TextView no_trailer_view;
        public ImageView shareTrailerView;

        public TrailerViewHolder(View v) {
            super(v);
            trailer_play = (Button) v.findViewById(R.id.play_trailer);
            no_trailer_view = (TextView)v.findViewById(R.id.no_trailer_message);
            shareTrailerView = (ImageView) v.findViewById(R.id.shareTrailer);
            trailer_play.setOnClickListener(this);
            shareTrailerView.setOnClickListener(this);
        }

        public Button getTrailerPlay() {
            return trailer_play;
        }

        @Override
        public void onClick(View view) {

            int viewId = view.getId();
            if(viewId == R.id.play_trailer){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse( String .format(mContext.getString(R.string.youtube_watch_uri),view.getTag())));
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );

                mContext.startActivity(intent);
            } else if(viewId == R.id.shareTrailer){
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, Uri.parse( String .format(mContext.getString(R.string.youtube_watch_uri),view.getTag())).toString());
                sendIntent.setType("text/plain");
                mContext.startActivity(Intent.createChooser(sendIntent, mContext.getResources().getText(R.string.send_to)));
            }


        }

        public TextView getNo_trailer_view() {
            return no_trailer_view;
        }

    }

    /*

           MOVIE VIEW HOLDER
    */



    //set moviedetails
    private void setMovieViewHolder(MovieViewHolder viewHolderMovie, int position) {
        Movies movies = (Movies) moviesList.get(position);
        if (movies != null) {

            movieTitle = movies.getMovies_title();
            viewHolderMovie.getMovieTitleView().setText(movies.getMovies_title());
            viewHolderMovie.getMovieTitleView().setContentDescription(String.format(mContext.getString(R.string.movie_detail_title_content_description),movies.getMovies_title()));
            viewHolderMovie.setMovieTitleView(viewHolderMovie.getMovieTitleView());

            viewHolderMovie.getMovieReleaseDateView().setText((movies.getRelease_date()));
            viewHolderMovie.getMovieReleaseDateView().setContentDescription(String.format(mContext.getString(R.string.movie_release), movies.getRelease_date()));

            String tmpRating = mContext.getResources().getString(R.string.movie_rating_text);
            viewHolderMovie.getMovieRatingView().setText(String.format(tmpRating, movies.getMovies_rating()));
            viewHolderMovie.getMovieRatingView().setContentDescription(String.format(tmpRating, movies.getMovies_rating()));

            viewHolderMovie.getMovieDescriptionView().setText(movies.getMovies_overview());
            viewHolderMovie.getMovieDescriptionView().setContentDescription(String.format(mContext.getString(R.string.movie_detail_overview_content_description),movies.getMovies_title(),movies.getMovies_overview()));

            Picasso
                    .with(mContext)
                    .load(mContext.getString(R.string.movie_db_poster_base_url)+mContext.getString(R.string.movies_db_poster_format)+movies.getMovies_image_url())
                    .into(viewHolderMovie.getPosterView());

            viewHolderMovie.getPosterView().setContentDescription(String.format(mContext.getString(R.string.movie_detail_image_content_description),movies.getMovies_title()));

            viewHolderMovie.getFavouriteButton().setTag(movies);
            viewHolderMovie.getFavouriteButton().setContentDescription(String.format(mContext.getString(R.string.add_movie_to_favourite_content_description),movies.getMovies_title()));

            //viewHolderMovie.getFavouriteButton().setTag(mContext.getString(R.string.movie_db_poster_base_url)+mContext.getString(R.string.movies_db_poster_format)+movies.getMovies_image_url());
            Boolean mIsFavourite;

            if (movies.getFavourite().equalsIgnoreCase("YES")) {
                mIsFavourite = true;
                viewHolderMovie.getFavouriteButton().setText(R.string.remove_from_favourites);
            } else {
                mIsFavourite = false;
                viewHolderMovie.getFavouriteButton().setText(mContext.getString(R.string.add_to_favourite));
            }

        }
    }


    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mMovieTitleView;
        TextView mMovieReleaseDateView;
        TextView mMovieRatingView;
        TextView mMovieDescriptionView;
        ImageView mMoviePosterView;
        Button mFavouriteButton;

        public MovieViewHolder(View root_view) {
            super(root_view);
            mMoviePosterView = (ImageView)root_view.findViewById(R.id.movie_image);
            mMovieTitleView = (TextView)root_view.findViewById(R.id.movie_title);
            mMovieDescriptionView = (TextView)root_view.findViewById(R.id.movie_overview);
            mMovieRatingView = (TextView)root_view.findViewById(R.id.movie_rating);
            mMovieReleaseDateView = (TextView)root_view.findViewById(R.id.movie_release);
            mFavouriteButton = (Button)root_view.findViewById(R.id.movie_favourite);
            mMoviePosterView.setOnClickListener(this);
            mFavouriteButton.setOnClickListener(this);
        }
        public TextView getMovieTitleView() {
            return mMovieTitleView;
        }

        public void setMovieTitleView(TextView titleView) {
            this.mMovieTitleView = titleView;
        }

        public TextView getMovieReleaseDateView() {
            return mMovieReleaseDateView;
        }

        public void setMovieReleaseDateView(TextView releaseDateView) {
            this.mMovieReleaseDateView = releaseDateView;
        }

        public TextView getMovieRatingView() {
            return mMovieRatingView;
        }

        public void setMovieRatingView(TextView ratingView) {
            this.mMovieRatingView = ratingView;
        }

        public TextView getMovieDescriptionView() {
            return mMovieDescriptionView;
        }

        public void setMovieDescriptionView(TextView descriptionView) {
            this.mMovieDescriptionView = descriptionView;
        }

        public ImageView getPosterView() {
            return mMoviePosterView;
        }

        public void setMoviePosterView(ImageView posterView) {
            this.mMoviePosterView = posterView;
        }

        public Button getFavouriteButton() {
            return mFavouriteButton;
        }

        public void setFavouriteButton(Button favouriteButton) {
            this.mFavouriteButton = favouriteButton;
        }

        @Override
        public void onClick(View view) {

            if(view.getId() == R.id.movie_favourite)
            {

                final Movies movie = (Movies)view.getTag();

                if(movie.getFavourite().equalsIgnoreCase(mContext.getString(R.string.yes)))

                {
                    ((Button)view).setContentDescription(String.format(mContext.getString(R.string.add_movie_to_favourite_content_description),movie.getMovies_title()));
                    movieValues = new ContentValues();
                    movieValues.put(MovieDataContract.MovieDataEntry.COLUMN_IS_FAVOURITE, mContext.getString(R.string.no));
                    movieValues.put(MovieDataContract.MovieDataEntry.COLUMN_POSTER_BLOB, new byte[0]);
                    ((Button)view).setText(mContext.getString(R.string.add_to_favourite));
                    updateData(movieValues,movie);


                }else {
                    ((Button)view).setText(mContext.getString(R.string.remove_from_favourites));
                    ((Button)view).setContentDescription(String.format(mContext.getString(R.string.remove_from_favourites_content_description),movie.getMovies_title()));

                    Thread r = new Thread() {
                        @Override
                        public void run() {
                            Bitmap bitmap = null;
                            try {
                                final RequestCreator image_load = Picasso.with(mContext)
                                        .load(mContext.getString(R.string.movie_db_poster_base_url)+mContext.getString(R.string.movies_db_poster_format)+movie.getMovies_image_url());
                                bitmap = image_load.get();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                movieValues = new ContentValues();
                                movieValues.put(MovieDataContract.MovieDataEntry.COLUMN_IS_FAVOURITE, mContext.getString(R.string.yes));
                                movieValues.put(MovieDataContract.MovieDataEntry.COLUMN_POSTER_BLOB, baos.toByteArray());

                                int updateRecord = updateData(movieValues,movie);
                                //check that update succeeded
                                if (updateRecord != 0) {


                                } else {
                                    Log.e(LOG_TAG, "Failed to update the record with favourite movies");

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    r.start();
                }
            }
        }

    }

    private int updateData(ContentValues movieValues, Movies movie)
    {
        //update
        int updateRecord = mContext.getContentResolver().update(MovieDataContract.MovieDataEntry.CONTENT_URI,
                movieValues, MovieDataContract.MovieDataEntry._ID + " = ?",
                new String[]{movie.getMovie_id()});

        return updateRecord;

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public void addObjects(ArrayList<Object> movie_object) {
        moviesList.addAll(movie_object);
    }

    public ArrayList<Object> getObjects()
    {
        return moviesList;
    }

    // added the get object method so it is possible to return an object from a certain position
    public Object getObject(int position) {
        if (moviesList.get(position) instanceof Trailer) {
            Trailer mTrailer = (Trailer) moviesList.get(position);
            return mTrailer;
        } else if (moviesList.get(position) instanceof Reviews) {
            Reviews mReview = (Reviews) moviesList.get(position);
            return mReview;
        } else if (moviesList.get(position) instanceof Movies) {
            Movies mMovie = (Movies) moviesList.get(position);
            return mMovie;
        }
        return null;
    }


}
