package com.fci.yehiahd.moviesapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by yehia on 19/03/16.
 */

public class Home extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().add(R.id.container, new HomeFragment()).commit();
        }
    }
    
}
