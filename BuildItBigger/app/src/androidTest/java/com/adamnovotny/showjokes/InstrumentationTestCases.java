package com.adamnovotny.showjokes;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.adamnovotny.showjokes.backend.myApi.MyApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.udacity.gradle.builditbigger.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class InstrumentationTestCases extends ActivityInstrumentationTestCase2 {
    Context mContext;
    String LOG_TAG = getClass().getSimpleName();

    public InstrumentationTestCases() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testAsync() {
        MyApi myApiService;
        MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                .setRootUrl("https://builtibig3.appspot.com/_ah/api/");
        myApiService = builder.build();
        String result = "";
        try {
            result = myApiService.sayHi("someName").execute().getData();
        } catch (IOException e) {
            Log.e("AppTest", e.toString());
        }
        assert result.length() > 2;
    }
}
