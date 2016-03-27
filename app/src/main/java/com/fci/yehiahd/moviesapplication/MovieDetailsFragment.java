package com.fci.yehiahd.moviesapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class MovieDetailsFragment extends Fragment {

    public MovieDetailsFragment(){

    }

    private TextView detailTitle,detailDate,detailVoteAverage,detailDescription;
    private ImageView detailImage;
    private Button detailMarkAsFavorite,trailersButton,reviewsButton;
    private MovieInfo movieInfo;
    StringBuilder builder;
    String titleTemp;
    RatingBar ratingBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie_details, container,false);

        detailTitle = (TextView) view.findViewById(R.id.detail_title);
        detailDate = (TextView) view.findViewById(R.id.detail_date);
        detailVoteAverage = (TextView) view.findViewById(R.id.detail_vote_average);
        detailDescription = (TextView) view.findViewById(R.id.detail_description);
        detailImage = (ImageView) view.findViewById(R.id.detail_image);
        detailMarkAsFavorite = (Button) view.findViewById(R.id.detail_mark_as_favorite);
        ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
        trailersButton = (Button) view.findViewById(R.id.trailers_button);
        reviewsButton = (Button) view.findViewById(R.id.reviews_button);


        movieInfo = getActivity().getIntent().getExtras().getParcelable("film");
        builder = new StringBuilder();
        titleTemp = movieInfo.getTitle();
        if(titleTemp.contains(":")){
            detailTitle.setText(titleTemp.substring(0, titleTemp.indexOf(':')));
        }

        else
            detailTitle.setText(titleTemp);


        trailersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), TrailersActivity.class);
                i.putExtra("object", movieInfo);
                startActivity(i);
            }
        });

        reviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),ReviewsActivity.class);
                i.putExtra("object",movieInfo);
                startActivity(i);
            }
        });


        detailDate.setText(movieInfo.getRelease_date().substring(0, 4));
        detailVoteAverage.setText(movieInfo.getVote_average()+"/10");
        detailDescription.setText(movieInfo.getOverview());
        ratingBar.setRating((float) movieInfo.getVote_average());
        ratingBar.setNumStars(10);
        builder.append("http://image.tmdb.org/t/p/").append("w185/").append(movieInfo.getPoster_path());
        Picasso.with(getActivity()).load(builder.toString()).placeholder(R.drawable.icon_loading).resize(300, 450).into(detailImage);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.refresh:
                getActivity().recreate();
                return true;
            case R.id.setting:
                startActivity(new Intent(getActivity(),SettingActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
