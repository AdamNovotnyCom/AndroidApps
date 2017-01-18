package com.udacity.gradle.builditbigger;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Defines Api constants
 * Reference tutorial: http://randomdotnext.com/retrofit-rxjava/
 */

public interface ApiService {
    String BASE_URL = "https://jsonplaceholder.typicode.com";

    @GET("/{collection}")
    Call<List<Joke>> getJoke(
            @Path("collection") String collection);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
