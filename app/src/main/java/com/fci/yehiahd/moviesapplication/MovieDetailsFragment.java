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
                Intent i = new Intent(getActivity(), ReviewsActivity.class);
                i.putExtra("object", movieInfo);
                startActivity(i);
            }
        });


        detailMarkAsFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = new DBConnection(getActivity());

                boolean check = db.checkID(movieInfo.getId());
                if(check == false){
                    Toast.makeText(getActivity(), "This movie already added to favorite list", Toast.LENGTH_SHORT).show();
                    return;
                }
                db.addMovie(movieInfo.getId(),movieInfo.getPoster_path(),movieInfo.getRelease_date(),movieInfo.getVote_average(),
                        movieInfo.getOverview(),movieInfo.getTitle());
                Toast.makeText(getActivity(), "Added successfully", Toast.LENGTH_SHORT).show();
            }
        });

        dateTemp = movieInfo.getRelease_date().substring(0,4);
        detailDate.setText(dateTemp);
        detailVoteAverage.setText(movieInfo.getVote_average() + "/10");
        detailDescription.setText(movieInfo.getOverview());
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
