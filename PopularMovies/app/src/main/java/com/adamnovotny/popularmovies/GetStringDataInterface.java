package com.adamnovotny.popularmovies;

import java.util.ArrayList;

public interface GetStringDataInterface {
    void onTaskCompleted(String source, ArrayList<String> data);
}
