package com.chitrahaar.darshan;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by obelix on 29/10/2016.
 */

public class MovieDetailFragment extends Fragment {

    private ImageView movie_image;
    private TextView movie_title;
    private TextView movie_overview;
    private TextView movie_rating;
    private Movies movies;
    private Resources res;
    private CardView movie_card_view;
    private TextView movie_release_date;

    //Zero Argument Constructor
    public MovieDetailFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root_view = inflater.inflate(R.layout.movies_detail_item,container,false);

        res = getResources();


        movie_card_view = (CardView)root_view.findViewById(R.id.movie_card_view);
        //Get the detail view movie passed in the Bundle to set to the view
        movie_image = (ImageView)root_view.findViewById(R.id.movie_image);
        movie_title = (TextView)root_view.findViewById(R.id.movie_title);
        movie_overview = (TextView)root_view.findViewById(R.id.movie_overview);
        movie_rating = (TextView)root_view.findViewById(R.id.movie_rating);
        movie_release_date = (TextView)root_view.findViewById(R.id.movie_release);
        Bundle arguments = getArguments();
        if(arguments != null)
        {
            movies = getArguments().getParcelable(res.getString(R.string.movies_detail_view));

            String movie_title_text = movies.getMovies_title();
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


            Picasso.with(getActivity())
                    .load(getString(R.string.movie_db_poster_base_url)+getString(R.string.movies_db_poster_format)+movies.getMovies_image_url())
                    .error(R.mipmap.ic_wifi)
                    .into(movie_image);
            movie_image.setContentDescription(String.format(res.getString(R.string.movie_detail_image_content_description),movie_title_text));


        }else{
            movie_card_view.setContentDescription(getString(R.string.no_internet_movie_detail));
            movie_title.setText(getString(R.string.no_internet_movie_detail));
        }



        return root_view;
    }

}
