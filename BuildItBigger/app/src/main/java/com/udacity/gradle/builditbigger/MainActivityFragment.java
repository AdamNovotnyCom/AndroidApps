package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.adamnovotny.showjokes.MainShowJokes;
import com.example.jokes.JokesMain;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.udacity.gradle.builditbigger.ApiService.retrofit;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        AdView mAdView = (AdView) root.findViewById(R.id.adView);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        setJokeBtn(root);

        return root;
    }

    // Set up for Favorite button
    private void setJokeBtn(View mainView) {
        Button jokeBtn =
                (Button) mainView.findViewById(R.id.joke_btn);
        jokeBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO REMOVE test
                callApi();

                // TODO get joke from java library
                JokesMain jokesmain = new JokesMain();
                String jokeStr = jokesmain.getJoke();
                Log.d("MainFragment", "Message from java lib: " + jokeStr);

                // TODO pass joke to Android library to show
                Intent intent = new Intent(getContext(), MainShowJokes.class);
                intent.putExtra("joke", jokeStr);
                startActivity(intent);

            }
        });
    }

    private void callApi() {
        Observer<String> stringObserver;
        stringObserver = new Observer<String>() {
            @Override
            public void onNext(String value) {
                // TODO value is the joke -> start activity showJoke
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.d("stringObserver", "onError", e);
            }
        };
        Observable observable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber subscriber) {
                        ApiService jokeService = retrofit.create(ApiService.class);
                        Call<List<Joke>> call = jokeService.getJoke("albums");
                        try {
                            List<Joke> result = call.execute().body();
                            Log.d(LOG_TAG, "Results fetched from Api: " +
                            result.get(0).getTitle());
                            subscriber.onNext("OK");
                            subscriber.onCompleted();
                        } catch (IOException e) {
                            Log.e(LOG_TAG, e.toString());
                        }

                    }
                })
                .subscribeOn(Schedulers.io()) // subscribeOn the I/O thread, not computationally intensive
                .observeOn(AndroidSchedulers.mainThread()); // observeOn the UI Thread
        observable.subscribe(stringObserver);
    }
}
