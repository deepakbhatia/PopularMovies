package com.chitrahaar.darshan;

import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

/**
 * Created by obelix on 22/11/2016.
 */

public class MovieGridAdapter extends CursorAdapter {
    public MovieGridAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public Object getItem(int position) {

        getCursor().moveToPosition(position);
        return getCursor();
    }

    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View convertView = LayoutInflater.from(context).inflate(R.layout.movies_item, parent, false);

        PosterHolder posterHolder = new PosterHolder(convertView);
        convertView.setTag(posterHolder);

        //TODO
        Log.d("PosterHolder", "PosterHolder");
        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        PosterHolder posterHolder = (PosterHolder) view.getTag();
        ImageView movie_image_view = posterHolder.posterView;

        final RequestCreator image_load = Picasso.with(context)
                .load(context.getString(R.string.movie_db_poster_base_url) + context.getString(R.string.movies_db_poster_format) + cursor.getString(MainActivityFragment.COLUMN_POSTER));

        //TODO
        Log.d("picasso", context.getString(R.string.movie_db_poster_base_url) + context.getString(R.string.movies_db_poster_format) + cursor.getString(MainActivityFragment.COLUMN_POSTER));
        image_load.into(movie_image_view);
    }

    /**
     * Cache of the children views for a poster list item.
     */
    public static class PosterHolder {
        public ImageView posterView;

        public PosterHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.movie_image);
        }
    }


    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }
}