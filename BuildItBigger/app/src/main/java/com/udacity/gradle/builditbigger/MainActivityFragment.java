package com.udacity.gradle.builditbigger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jokes.JokesMain;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

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

        // REMOVE
        // set joke text
        JokesMain jokesmain = new JokesMain();
        String jokeStr = jokesmain.getJoke();
        TextView jokeTV = (TextView) root.findViewById(R.id.joke_text_view);
        jokeTV.setText(jokeStr);

        return root;
    }

    public void tellJoke(View view) {
        Toast.makeText(getContext(), "derp", Toast.LENGTH_SHORT).show();
    }

    // Set up for Favorite button
    private void setJokeBtn(View mainView) {
        Button jokeBtn =
                (Button) mainView.findViewById(R.id.joke_btn);
        jokeBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "derp 2", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
