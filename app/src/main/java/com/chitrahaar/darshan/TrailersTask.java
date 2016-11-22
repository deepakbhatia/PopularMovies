package com.chitrahaar.darshan;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
 * Created by obelix on 09/11/2016.
 */

public class TrailersTask extends AsyncTask<String, String, ArrayList<Object>> {

    private final String LOG_TAG = TrailersTask.class.getSimpleName();

    private Context context;

    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String moviesJSON = null;

    private boolean network_error = false;

    private boolean parse_error = false;

    private MoviesViewAdapter moviesViewAdapter;

    public TrailersTask(Context context, MoviesViewAdapter moviesViewAdapter)
    {
        this.context = context;

        this.moviesViewAdapter = moviesViewAdapter;

    }

    @Override
    protected ArrayList<Object> doInBackground(String[] params) {
        String video_id = params[0];
        String url_string = String.format(context.getString(R.string.movie_db_base_url)+context.getString(R.string.find_trailer),video_id);

        ArrayList<Object> trailer_list = null;

        try{
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .encodedAuthority(url_string)
                    .appendQueryParameter(context.getString(R.string.movie_db_api_key_param),BuildConfig.MOVIE_API_KEY);

            String movie_db_query_url = builder.build().toString();

            //TODO
            Log.d(LOG_TAG,movie_db_query_url);
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

            trailer_list = parse(moviesJSON);

        }
        catch (UnknownHostException ex)
        {
            network_error = true;
            trailer_list = null;
        }
        catch (IOException ex)
        {
            parse_error = true;
            trailer_list = null;
            Log.e(LOG_TAG,ex.toString());
        }
        return trailer_list;
    }

    private ArrayList<Object> parse(String trailer){

        //TODO
        Log.d(LOG_TAG,trailer);


        String trailer_id = null;

        ArrayList<Object> trailer_list = new ArrayList<Object>();

        try {
            JSONObject trailersJSONObject = new JSONObject(trailer);

            JSONArray trailerJSONArray = trailersJSONObject.getJSONArray(context.getString(R.string.movies_db_results_json_tag));

            for(int trailers_index=0 ; trailers_index< trailerJSONArray.length(); trailers_index++)
            {
                JSONObject trailerObject = trailerJSONArray.getJSONObject(trailers_index);

                //TODO CLEAN
                Log.d(LOG_TAG,trailerObject.toString());

                trailer_id = trailerObject.getString("key");

                String trailer_name = trailerObject.getString("name");

                Trailer trailer1 = new Trailer(trailer_id,trailer_name);

                trailer_list.add(trailer1);

            }


        } catch (JSONException e) {

            trailer_list = null;
            e.printStackTrace();
        }

        //TODO
        Log.d(LOG_TAG,"trailer:"+trailer_id);


        return trailer_list;
    }
    @Override
    protected void onPostExecute(ArrayList<Object> trailers) {
        //TODO
        //Log.d(LOG_TAG,result);
        ViewHeaders trailer_header = new ViewHeaders(context.getString(R.string.trailers_section_label));

        ArrayList<Object> headerList = new ArrayList<>();
        headerList.add(trailer_header);
        moviesViewAdapter.addObjects(headerList);
        if(trailers !=null && trailers.size() > 0)
        {
            moviesViewAdapter.addObjects(trailers);
        }
        else{

            ArrayList<Object> empty_trailer_list = new ArrayList<>();

            Trailer empty_trailer = new Trailer(null,null);

            empty_trailer_list.add(empty_trailer);
            moviesViewAdapter.addObjects(empty_trailer_list);

        }
        moviesViewAdapter.notifyDataSetChanged();
    }
}
