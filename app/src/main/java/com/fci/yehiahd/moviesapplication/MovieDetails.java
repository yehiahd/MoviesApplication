package com.fci.yehiahd.moviesapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {

    private TextView detailTitle,detailDate,detailVoteAverage,detailDescription;
    private ImageView detailImage;
    private Button detailMarkAsFavorite;
    private MovieInfo movieInfo;
    StringBuilder builder;
    String titleTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        setTitle("MovieDetail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);


        detailTitle = (TextView) findViewById(R.id.detail_title);
        detailDate = (TextView) findViewById(R.id.detail_date);
        detailVoteAverage = (TextView) findViewById(R.id.detail_vote_average);
        detailDescription = (TextView) findViewById(R.id.detail_description);
        detailImage = (ImageView) findViewById(R.id.detail_image);
        detailMarkAsFavorite = (Button) findViewById(R.id.detail_mark_as_favorite);

        movieInfo = getIntent().getExtras().getParcelable("film");
        builder = new StringBuilder();
        titleTemp = movieInfo.getTitle();

        if(titleTemp.contains(":")){
            detailTitle.setText(titleTemp.substring(0, titleTemp.indexOf(':')));
        }

        else
            detailTitle.setText(titleTemp);

        detailDate.setText(movieInfo.getRelease_date().substring(0,4));
        detailVoteAverage.setText(movieInfo.getVote_average()+"/10");
        detailDescription.setText(movieInfo.getOverview());
        builder.append("http://image.tmdb.org/t/p/").append("w185/").append(movieInfo.getPoster_path());
        Picasso.with(this).load(builder.toString()).placeholder(R.drawable.icon_loading).resize(300,450).into(detailImage);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.refresh:
                this.recreate();
                return true;
            case R.id.setting:
                startActivity(new Intent(MovieDetails.this,SettingActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
