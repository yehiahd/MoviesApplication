package com.fci.yehiahd.moviesapplication;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


public class MovieDetailsFragment extends Fragment {

    public MovieDetailsFragment(){

    }

    private TextView detailTitle,detailDate,detailVoteAverage,detailDescription;
    private ImageView detailImage;
    private Button detailMarkAsFavorite,trailersButton,reviewsButton;
    private MovieInfo movieInfo;
    StringBuilder builder;
    String titleTemp ,dateTemp;
    DBConnection db;

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
        trailersButton = (Button) view.findViewById(R.id.trailers_button);
        reviewsButton = (Button) view.findViewById(R.id.reviews_button);

        if(!isTablet(getActivity())){
            movieInfo = getActivity().getIntent().getExtras().getParcelable("film");
            builder = new StringBuilder();
            titleTemp = movieInfo.getTitle();

            if(titleTemp.contains(":")){
                detailTitle.setText(titleTemp.substring(0, titleTemp.indexOf(':')));
            }

            else
                detailTitle.setText(titleTemp);

            dateTemp = movieInfo.getRelease_date().substring(0,4);
            detailDate.setText(dateTemp);
            detailVoteAverage.setText(movieInfo.getVote_average() + "/10");
            detailDescription.setText(movieInfo.getOverview());
            builder.append("http://image.tmdb.org/t/p/").append("w185/").append(movieInfo.getPoster_path());
            Picasso.with(getActivity()).load(builder.toString()).placeholder(R.drawable.icon_loading).error(R.drawable.error).resize(300, 450).into(detailImage);
            markUnMark();
        }

        trailersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkConnection()){
                    Intent i = new Intent(getActivity(), TrailersActivity.class);
                    i.putExtra("object", movieInfo);
                    startActivity(i);
                }

                else
                    Toast.makeText(getActivity(), "please check your Internet Connection", Toast.LENGTH_SHORT).show();

            }
        });

        reviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkConnection()){
                    Intent i = new Intent(getActivity(), ReviewsActivity.class);
                    i.putExtra("object", movieInfo);
                    startActivity(i);
                }

                else
                    Toast.makeText(getActivity(), "please check your Internet Connection", Toast.LENGTH_SHORT).show();

            }
        });


        detailMarkAsFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = new DBConnection(getActivity());

                boolean check = db.checkID(movieInfo.getId());
                if (check == false) {
                    Toast.makeText(getActivity(), "Removed From Favorite list", Toast.LENGTH_SHORT).show();
                    markUnMark();
                    return;
                }
                db.addMovie(movieInfo.getId(), movieInfo.getPoster_path(), movieInfo.getRelease_date(), movieInfo.getVote_average(),
                        movieInfo.getOverview(), movieInfo.getTitle());
                Toast.makeText(getActivity(), "Added to favorite List", Toast.LENGTH_SHORT).show();
                markUnMark();
            }
        });

        return view;
    }

    public boolean checkConnection(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void changeDate(MovieInfo movieInfo){

        if(movieInfo.getId().equals("")){
            detailTitle.setText("No Favorites");
            detailImage.setVisibility(View.GONE);
            reviewsButton.setVisibility(View.GONE);
            detailMarkAsFavorite.setVisibility(View.GONE);
            trailersButton.setVisibility(View.GONE);
            detailDate.setVisibility(View.GONE);
            detailVoteAverage.setVisibility(View.GONE);
            detailDescription.setVisibility(View.GONE);
            return;
        }

        else
        {
            detailImage.setVisibility(View.VISIBLE);
            reviewsButton.setVisibility(View.VISIBLE);
            detailMarkAsFavorite.setVisibility(View.VISIBLE);
            trailersButton.setVisibility(View.VISIBLE);
            detailDate.setVisibility(View.VISIBLE);
            detailVoteAverage.setVisibility(View.VISIBLE);
            detailDescription.setVisibility(View.VISIBLE);
        }

        this.movieInfo = movieInfo;

        builder = new StringBuilder();
        titleTemp = movieInfo.getTitle();

        if(titleTemp.contains(":")){
            detailTitle.setText(titleTemp.substring(0, titleTemp.indexOf(':')));
        }

        else
            detailTitle.setText(titleTemp);

        dateTemp = movieInfo.getRelease_date().substring(0,4);
        detailDate.setText(dateTemp);
        detailVoteAverage.setText(movieInfo.getVote_average() + "/10");
        detailDescription.setText(movieInfo.getOverview());
        builder.append("http://image.tmdb.org/t/p/").append("w185/").append(movieInfo.getPoster_path());
        Picasso.with(getActivity()).load(builder.toString()).placeholder(R.drawable.icon_loading).error(R.drawable.error).resize(300, 450).into(detailImage);
        markUnMark();

    }


    public void markUnMark(){
        db = new DBConnection(getActivity());
        if(db.isFavorite(movieInfo.getId())){
            detailMarkAsFavorite.setBackgroundColor(Color.YELLOW);
            detailMarkAsFavorite.setText("Marked as Favorite");
        }

        else {
            detailMarkAsFavorite.setBackgroundColor(Color.parseColor("#00daaf"));
            detailMarkAsFavorite.setText("Mark as Favorite");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(!isTablet(getActivity()))
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(!isTablet(getActivity())){
            switch (item.getItemId()){
                case R.id.refresh:
                    getActivity().recreate();
                    return true;
                case R.id.setting:
                    startActivity(new Intent(getActivity(),SettingActivity.class));
                    return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}
