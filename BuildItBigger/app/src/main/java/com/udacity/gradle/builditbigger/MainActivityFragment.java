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
import com.adamnovotny.showjokes.backend.myApi.MyApi;
import com.example.jokes.JokesMain;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


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
                        Boolean productionBackend = false;
                        MyApi myApiService;
                        if (productionBackend) {
                            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                                    new AndroidJsonFactory(), null)
                                    .setRootUrl("https://builtibig3.appspot.com/_ah/api/");
                            myApiService = builder.build();
                        }
                        else {
                            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                                    new AndroidJsonFactory(), null)
                                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                                        @Override
                                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                            abstractGoogleClientRequest.setDisableGZipContent(true);
                                        }
                                    });
                            myApiService = builder.build();
                        }
                        try {
                            String result = myApiService.sayHi("someName").execute().getData();
                            Log.d(LOG_TAG, "Results fetched from Api (prod version = " +
                                    productionBackend + ") : " + result);
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
