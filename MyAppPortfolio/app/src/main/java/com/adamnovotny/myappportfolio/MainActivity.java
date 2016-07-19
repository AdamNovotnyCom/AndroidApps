package com.adamnovotny.myappportfolio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void launchAppOnclick(View v) {
        switch(v.getId()) {
            case R.id.button_popular_movies:
                Toast toast1 = Toast.makeText(getApplicationContext(),
                        "This button will launch my Popular Movies App",
                        Toast.LENGTH_SHORT);
                toast1.show();
                break;
            case R.id.button_stock_hawk:
                Toast toast2 = Toast.makeText(getApplicationContext(),
                        "This button will launch my Stock Hawk App",
                        Toast.LENGTH_SHORT);
                toast2.show();
                break;
            case R.id.button_build_it_bigger:
                Toast toast3 = Toast.makeText(getApplicationContext(),
                        "This button will launch my Build It Bigger App",
                        Toast.LENGTH_SHORT);
                toast3.show();
                break;
            case R.id.button_make_your_app_material:
                Toast toast4 = Toast.makeText(getApplicationContext(),
                        "This button will launch my Make Your App Material App",
                        Toast.LENGTH_SHORT);
                toast4.show();
                break;
            case R.id.button_go_ubiquitous:
                Toast toast5 = Toast.makeText(getApplicationContext(),
                        "This button will launch my Go Ubiquitous App",
                        Toast.LENGTH_SHORT);
                toast5.show();
                break;
            case R.id.button_capstone:
                Toast toast6 = Toast.makeText(getApplicationContext(),
                        "This button will launch my Capstone App",
                        Toast.LENGTH_SHORT);
                toast6.show();
                break;
            default:
                throw new RuntimeException("Unknow button ID");
        }
    }
}
