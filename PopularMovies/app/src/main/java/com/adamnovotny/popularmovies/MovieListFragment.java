package com.adamnovotny.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends Fragment {
    GridView moviesGrid;
    MoviesAdapter moviesAdapter;
    ArrayList<MovieParcelable> moviesP = new ArrayList<>();

    public MovieListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            updateMovies();
        }
        else {
            moviesP = savedInstanceState.getParcelableArrayList("movies");
        }
        setHasOptionsMenu(true);
        setPrefListener();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.movie_list_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getContext(), MySettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment, populate content, set onclick
        View gridView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        moviesGrid = (GridView) gridView.findViewById(R.id.moviesGridView);
        moviesAdapter = new MoviesAdapter(getActivity(), moviesP);
        moviesGrid.setAdapter(moviesAdapter);
        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO launch detail activity
                Log.i("MovieListFragment", "Pressed: " + position); /////////////////////////

            }
        });
        return gridView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies", moviesP);
    }

    private void setPrefListener() {
        SharedPreferences.OnSharedPreferenceChangeListener prefListener;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                switch (key) {
                    case "sort_type":
                        updateMovies();
                        break;
                }
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }

    private void updateMovies() {
        GetMovieData movieDataTask = new GetMovieData();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortType = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popularity));
        switch (sortType) {
            case "Popularity":
                movieDataTask.execute("popularity.desc");
                break;
            case "Vote average":
                movieDataTask.execute("vote_average.desc");
                break;
            default:
                movieDataTask.execute("popularity.desc");
                break;
        }
    }

    class MoviesAdapter extends BaseAdapter {
        ArrayList<MovieParcelable> movies;
        Context context;
        LayoutInflater inflater;

        public MoviesAdapter(Context cont, ArrayList<MovieParcelable> m) {
            context = cont;
            movies = m;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return movies.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_movie_list_item, null);
            }
            ImageView img = (ImageView) view.findViewById(R.id.fragment_movie_list_item_imageview);
            String baseUrl = "http://image.tmdb.org/t/p/w185/";
            String imgUrl = movies.get(i).image;
            Picasso.with(context).load(baseUrl + imgUrl).into(img);
            return view;
        }
    }

    private class GetMovieData extends AsyncTask<String, Void, ArrayList<MovieParcelable>> {
        private final String LOG_TAG = GetMovieData.class.getSimpleName();

        @Override
        protected ArrayList<MovieParcelable> doInBackground(String... params) {
            if (params.length == 0) { // error
                return null;
            }

            // Connection objects declared outside try block to be closed in finally
            HttpURLConnection urlConn = null;
            BufferedReader reader = null;
            String urlConnResult;
            try {
                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String PARAM_SORT = "sort_by";
                final String PARAM_KEY = "api_key";
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(PARAM_SORT, params[0])
                        .appendQueryParameter(PARAM_KEY, AppKeys.themoviedb)
                        .build();
                URL url = new URL(builtUri.toString());
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("GET");
                urlConn.connect();
                InputStream inputStream = urlConn.getInputStream();
                // read input
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                urlConnResult = buffer.toString();
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }
            finally {
                if (urlConn != null) {
                    urlConn.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return updateMoviesFromJson(urlConnResult);
        }

        @Override
        protected void onPostExecute(ArrayList<MovieParcelable> movies) {
            moviesP = movies;
            moviesAdapter = new MoviesAdapter(getActivity(), moviesP);
            moviesGrid.setAdapter(moviesAdapter);
            Log.i(LOG_TAG, "JSON data received"); ///////////////////////////////
        }

        private ArrayList<MovieParcelable> updateMoviesFromJson(String jsonStr) {
            ArrayList<MovieParcelable> movies = new ArrayList<>();
            try {
                JSONObject moviesJson = new JSONObject(jsonStr);
                JSONArray moviesArray = moviesJson.getJSONArray("results");
                for(int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    MovieParcelable movie = new MovieParcelable(
                            movieObj.getString("title"),
                            movieObj.getString("poster_path"),
                            movieObj.getString("overview"),
                            movieObj.getString("vote_average"),
                            movieObj.getString("release_date"));
                    movies.add(movie);
                }
            }
            catch (JSONException e) {
                Log.e(LOG_TAG, "JSON exception: ", e);
            }
            return movies;
        }
    }
}
