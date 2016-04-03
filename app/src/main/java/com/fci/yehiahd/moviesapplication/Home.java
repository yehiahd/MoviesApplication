package com.fci.yehiahd.moviesapplication;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by yehia on 19/03/16.
 */

public class Home extends AppCompatActivity implements Communicator{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(!isTablet(this)){
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction().add(R.id.container, new HomeFragment()).commit();
            }
        }

    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void respond(MovieInfo movieInfo) {
        MovieDetailsFragment fragment = (MovieDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.container_detail_fragment_tablet);
        fragment.changeDate(movieInfo);
    }

}
