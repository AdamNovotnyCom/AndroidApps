package com.adamnovotny.showjokes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainShowJokes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_show_jokes);
        Intent intent = getIntent();
        setJokeTxt(intent);
    }

    private void setJokeTxt(Intent intent) {
        TextView jokeTxt =
                (TextView) findViewById(R.id.joke_content);
        jokeTxt.setText(intent.getStringExtra("joke"));
    }
}
