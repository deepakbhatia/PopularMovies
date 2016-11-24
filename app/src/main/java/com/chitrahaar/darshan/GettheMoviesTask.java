package com.chitrahaar.darshan;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by obelix on 22/10/2016.
 */

public class GettheMoviesTask extends AsyncTask<String,String,ArrayList<Movies>> {

    private final String LOG_TAG = GettheMoviesTask.class.getSimpleName();

    private Context context;

    private ReturnMovies returnMovies;

    private boolean network_error = false;

    private boolean parse_error = false;
    public interface ReturnMovies{
        public void returnMovies(ArrayList<Movies> moviesList, boolean network_error, boolean parse_error);
    }

    //Zero Argument Constructor
    public GettheMoviesTask() {}

    public GettheMoviesTask(@NonNull Context context, @NonNull ReturnMovies returnMovies)
    {
        this.context = context;

        this.returnMovies = returnMovies;
    }


    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String moviesJSON = null;

    @Override
    protected @Nullable ArrayList<Movies> doInBackground(String[] params) {

        String sort_type = params[0];
        String url_string = context.getString(R.string.movie_db_base_url)+sort_type;

        ArrayList<Movies> moviesList = null;

        try{
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .encodedAuthority(url_string)
                    .appendQueryParameter(context.getString(R.string.movie_db_api_key_param),BuildConfig.MOVIE_API_KEY);

            String movie_db_query_url = builder.build().toString();


            //Build the URL Object
            URL url = new URL(movie_db_query_url);

            // Create the request to MoviesDB API, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");

            urlConnection.connect();


            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            //No inputStream available
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            moviesJSON = buffer.toString();

            moviesList = getMoviesList(moviesJSON);

        }
        catch (UnknownHostException ex)
        {
            network_error = true;
            if(moviesList == null)
            {
                Log.e(LOG_TAG,ex.toString());

            }
        }
        catch (IOException ex)
        {
            parse_error = true;
            Log.e(LOG_TAG,ex.toString());
        }
        return moviesList;
    }

    @Override
    protected void onPostExecute(ArrayList<Movies> movies) {
        super.onPostExecute(movies);
        MainActivityFragment.progressBar.setVisibility(View.GONE);
        returnMovies.returnMovies(movies,network_error,parse_error);
    }


    private ArrayList<Movies> getMoviesList(@NonNull String moviesJSON)
    {

        //List of movies for adapter
        ArrayList<Movies> movies_list = new ArrayList<Movies>();
        try {
            JSONObject moviesJSONObject = new JSONObject(moviesJSON);

            JSONArray moviesJSONArray = moviesJSONObject.getJSONArray(context.getString(R.string.movies_db_results_json_tag));
            
            for(int movies_index=0 ; movies_index< moviesJSONArray.length(); movies_index++)
            {
                JSONObject movieObject = moviesJSONArray.getJSONObject(movies_index);

                Movies movie = new Movies(
                        movieObject.getString(context.getString(R.string.movies_db_results_original_title)),

                        movieObject.getString(context.getString(R.string.movies_db_results_json_poster_path)),
                
                        movieObject.getString(context.getString(R.string.movies_db_results_overview)),

                        movieObject.getDouble(context.getString(R.string.movies_db_results_user_rating)),

                        movieObject.getString(context.getString(R.string.movies_db_results_original_language)),

                        movieObject.getString(context.getString(R.string.release_date))

                );

                movie.setMovie_id(movieObject.getString("id"));
                movies_list.add(movie);

            }
            
            
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies_list;
    }
}
