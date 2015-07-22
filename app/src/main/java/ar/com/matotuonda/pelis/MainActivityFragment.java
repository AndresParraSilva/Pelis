package ar.com.matotuonda.pelis;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = ImageAdapter.class.getSimpleName();
    private GridView gridView;
    private ImageAdapter mMovieAdapter;
    //String[] items = {};
    /*= {
            "/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg",
            "/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg",
            "/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg",
            "/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg",
            "/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg",
            "/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg",
            "/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg",
            "/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg",
            "/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg",
            "/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg",
            "/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg"
    };*/
    public List itemList= new ArrayList<String>();

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //updateMovies();
        Log.v(LOG_TAG, "En onCreate: " + Integer.toString(itemList.size()));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        if (gridView == null) {
            Log.e(LOG_TAG, "gridView nulo");
        }
        mMovieAdapter = new ImageAdapter(getActivity(), itemList);
        if (mMovieAdapter == null) {
            Log.e(LOG_TAG, "mMovieAdapter nulo");
        }
        gridView.setAdapter(mMovieAdapter);
        Log.v(LOG_TAG, "En onCreateView: " + Integer.toString(itemList.size()));
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "En onStart: " + Integer.toString(itemList.size()));
        updateMovies();
    }

    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //String location = prefs.getString(getString(R.string.pref_location_key),
        //        getString(R.string.pref_location_default));
        moviesTask.execute("popularity.desc");
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /* The date/time conversion code is going to be moved outside the asynctask later,
         * so for convenience we're breaking it out into its own method now.
         */
        private String getReadableDateString(long time) {
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        private String[] getMoviesDataFromJson(String moviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "results";
            final String OWM_TITLE = "origilnal_title";
            final String OWM_POSTER = "poster_path";
            final String OWM_OVERVIEW = "overview";
            final String OWM_VOTE_AVG = "vote_average";
            final String OWM_RELEASE = "release_date";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] items = new String[moviesArray.length()];

            // Data is fetched in Celsius by default.
            // If user prefers to see in Fahrenheit, convert the values here.
            // We do this rather than fetching in Fahrenheit so that the user can
            // change this option without us having to re-fetch the data once
            // we start storing the values in a database.
            /* SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
           String unitType = sharedPrefs.getString(
                    getString(R.string.pref_units_key),
                    getString(R.string.pref_units_metric));
*/

            for (int i = 0; i < moviesArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String poster;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject aMovie = moviesArray.getJSONObject(i);

                poster = aMovie.getString(OWM_POSTER);

                Log.v(LOG_TAG, poster);

                items[i] = poster;
            }
            return items;

        }

        @Override
        protected String[] doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            String size = "w342";
            //String sort_by= "popularity.desc";
            //String format = "json";
            //String units = "metric";
            String apiKey = "9d18324ae9bb6df35163f892f5e0622e";
            //int numDays = 7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String MOVIES_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                //"http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "sort_by";
                final String API_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(API_PARAM, apiKey)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                Log.v(LOG_TAG, moviesJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            Log.v(LOG_TAG, "Valores: " + Integer.toString(result.length) + " En items: " + Integer.toString(itemList.size()));
            if (result != null) {
                //items= result.clone();
                mMovieAdapter.posters.clear();
                itemList.clear();
                //mForecastAdapter.clear();
                for (String unPoster : result) {
                    mMovieAdapter.posters.add(unPoster);
                    itemList.add(unPoster);
                    Log.v(LOG_TAG, "Agregado: " + unPoster);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }
}