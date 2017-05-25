package com.great.tudien;



import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;

import android.support.v7.app.AppCompatActivity;

import android.view.Menu;



import java.util.ArrayList;


public class  MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Data.data = new Data(MainActivity.this);
                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                MainActivity.this.startActivity(intent);
                MainActivity.this.finish();
            }
        }, 300);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }






}
