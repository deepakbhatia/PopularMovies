package com.chitrahaar.darshan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by obelix on 11/11/2016.
 */

public class ReviewsAdapter extends ArrayAdapter<Reviews> {
    private Context context;
    private LayoutInflater inflater;
    private String movie_title;

    private ArrayList<Reviews> reviewsList;

    public ReviewsAdapter(@NonNull Context context, ArrayList<Reviews> reviewsList, String movie_title) {
        super(context, R.layout.reviews_item, reviewsList);

        this.context = context;
        this.reviewsList = reviewsList;

        this.movie_title = movie_title;

        inflater = LayoutInflater.from(context);
    }

    public int getCount() {

        return this.reviewsList.size();
    }

    @Override
    public Reviews getItem(int position) {
        return this.reviewsList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.reviews_item, parent, false);
        }

        //Create Image View for the Movie Thumbnail
        convertView.setContentDescription(String.format(context.getString(R.string.movie_poster_in_list),movie_title));
        TextView review_author = (TextView) convertView.findViewById(R.id.review_author);

        TextView review_content = (TextView) convertView.findViewById(R.id.review_content);

        review_author.setText(reviewsList.get(position).getAuthor());
        review_content.setText(reviewsList.get(position).getReview());
        return convertView;
    }

}
