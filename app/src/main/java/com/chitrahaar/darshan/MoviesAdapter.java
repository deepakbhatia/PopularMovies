package com.chitrahaar.darshan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;

/**
 * Created by obelix on 21/10/2016.
 */

public class MoviesAdapter extends ArrayAdapter<Movies> {
    private Context context;
    private LayoutInflater inflater;
    private ImageView movie_image_view;

    private ArrayList<Movies> moviesList;



    public MoviesAdapter(@NonNull Context context, ArrayList<Movies> moviesList) {
        super(context, R.layout.movies_item, moviesList);

        this.context = context;
        this.moviesList = moviesList;

        inflater = LayoutInflater.from(context);

    }

    public int getCount() {
        return this.moviesList.size();
    }

    @Override
    public Movies getItem(int position) {
        return this.moviesList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.movies_item, parent, false);
        }

        //Create Image View for the Movie Thumbnail
        convertView.setContentDescription(String.format(context.getString(R.string.movie_poster_in_list),this.moviesList.get(position).getMovies_title()));
        movie_image_view = (ImageView) convertView.findViewById(R.id.movie_image);

        final RequestCreator image_load = Picasso.with(context)
                .load(context.getString(R.string.movie_db_poster_base_url)+context.getString(R.string.movies_db_poster_format)+this.moviesList.get(position).getMovies_image_url());


        image_load.into(movie_image_view);







        return convertView;
    }


}
