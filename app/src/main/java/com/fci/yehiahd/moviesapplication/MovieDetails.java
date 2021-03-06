package com.fci.yehiahd.moviesapplication;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MovieDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        setTitle("MovieDetail");

        if(!isTablet(this)){
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction().add(R.id.container_movie_detail, new MovieDetailsFragment()).commit();
            }
        }

    }


    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}
