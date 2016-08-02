package com.adamnovotny.popularmovies;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends Fragment {
    GridView moviesGrid;
    // TODO populate movies from API
    int movies[] = {R.drawable.img1, R.drawable.img1, R.drawable.img1,
            R.drawable.img1, R.drawable.img1};

    public MovieListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment, populate content, set onclick
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        moviesGrid = (GridView) view.findViewById(R.id.moviesGridView);
        MoviesAdapter moviesAdapter = new MoviesAdapter(getActivity(), movies);
        moviesGrid.setAdapter(moviesAdapter);
        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // start-media code could go in fragment or adapter
                Log.i("MovieListFragment", "Clicked: " + position);
            }
        });
        return view;
    }

    class MoviesAdapter extends BaseAdapter {
        int movies[];
        LayoutInflater inflater;

        public MoviesAdapter(Context context, int[] m) {
            movies = m;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return movies.length;
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
            view = inflater.inflate(R.layout.fragment_movie_list_item, null);
            ImageView img = (ImageView) view.findViewById(R.id.fragment_movie_list_item_imageview);
            img.setImageResource(movies[i]);
            return view;
        }
    }
}
