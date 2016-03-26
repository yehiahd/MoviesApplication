package com.fci.yehiahd.moviesapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MovieDetailsFragment extends Fragment implements ListView.OnItemClickListener {

    public MovieDetailsFragment(){

    }

    private TextView detailTitle,detailDate,detailVoteAverage,detailDescription;
    private ImageView detailImage;
    private Button detailMarkAsFavorite;
    private MovieInfo movieInfo;
    StringBuilder builder;
    String titleTemp;
    ListView trailerList,reviewList;
    String trailerID;
    ArrayList<TrailerData> list ;
    ArrayList<ReviewData> list2;


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
        trailerList = (ListView) view.findViewById(R.id.detail_trailer_list_view);
        reviewList = (ListView) view.findViewById(R.id.detail_reviews_list_view);

        movieInfo = getActivity().getIntent().getExtras().getParcelable("film");
        builder = new StringBuilder();
        titleTemp = movieInfo.getTitle();
        trailerID = movieInfo.getId();

        if(titleTemp.contains(":")){
            detailTitle.setText(titleTemp.substring(0, titleTemp.indexOf(':')));
        }

        else
            detailTitle.setText(titleTemp);


        trailerList.setOnItemClickListener(this);
        detailDate.setText(movieInfo.getRelease_date().substring(0, 4));
        detailVoteAverage.setText(movieInfo.getVote_average()+"/10");
        detailDescription.setText(movieInfo.getOverview());
        builder.append("http://image.tmdb.org/t/p/").append("w185/").append(movieInfo.getPoster_path());
        Picasso.with(getActivity()).load(builder.toString()).placeholder(R.drawable.icon_loading).resize(300, 450).into(detailImage);
        new TrailerTask(getActivity()).execute();
        new ReviewTask(getActivity()).execute();
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).getKey())));
    }

    private class TrailerTask extends AsyncTask<Void,Void,TrailerData[]>{

        private Context mContext;
        TrailerTask(Context context){
            this.mContext=context;
        }


        @Override
        protected TrailerData[] doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String trailerJsonStr = null;



            try {
                String POSTER_BASE_URL = "https://api.themoviedb.org/3/movie/";
                POSTER_BASE_URL+=trailerID;
                POSTER_BASE_URL+="/videos?";

                final String APP_ID ="api_key";

                Uri uri = Uri.parse(POSTER_BASE_URL).buildUpon()
                        .appendQueryParameter(APP_ID,getString(R.string.api_key)).build();

                URL url = new URL(uri.toString());

                //Log.d("YehiaaaaaaaaaaaaaaaURL=",uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                trailerJsonStr = buffer.toString();
            }

            catch (IOException e){
                return null;
            }

            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {}
                }
            }
            try {
                return getTrailerDataFromJson(trailerJsonStr);
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(TrailerData[] trailerDatas) {

            if(trailerDatas!=null){
                list = new ArrayList<>();
                for (TrailerData temp : trailerDatas){
                    list.add(temp);
                    //Log.d("tgrobaaaaaaaaaaa",prefStatus);
                    //Log.d("id",temp.getId());
                    //Log.d("Name",temp.getName()+"a");
                    //Log.d("Key",temp.getKey());
                    //Log.d("Site",temp.getSite());
                }
                //Log.d("Yehiaaaaaaaaaaaaaaaaaa","el list keda tmam");
            }
            trailerList.setAdapter(new TrailerAdapter(mContext,list));
        }



        public TrailerData [] getTrailerDataFromJson(String trailerJsonStr) {

            final String OWN_LIST ="results";
            final String TRAILER_ID="id";
            final String TRAILER_KEY="key";
            final String TRAILER_NAME = "name";
            final String TRAILER_SITE="site";
            final String TRAILER_SIZE ="size";
            final String TRAILER_TYPE ="type";

            StringBuilder path ;
            String id,key,name,site , type;
            int size;

            JSONObject jsonRootObject = null;
            try {
                jsonRootObject = new JSONObject(trailerJsonStr);
            } catch (JSONException e) {
            }

            JSONArray jsonArray = jsonRootObject.optJSONArray(OWN_LIST);
            TrailerData [] trailerInfoArray = new TrailerData[jsonArray.length()];

            for(int i=0;i<jsonArray.length();i++){
                //Log.d("i",String.valueOf(i));
                path = new StringBuilder();
                trailerInfoArray[i] = new TrailerData();

                JSONObject jsonObject = null;
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                } catch (JSONException e) {

                }

                id = jsonObject.optString(TRAILER_ID).toString();
                path.append("https://www.youtube.com/watch?v=").append(jsonObject.optString(TRAILER_KEY).toString());
                key = String.valueOf(path);
                name = jsonObject.optString(TRAILER_NAME).toString();
                site = jsonObject.optString(TRAILER_SITE).toString();
                size = jsonObject.optInt(TRAILER_SIZE);
                type = jsonObject.optString(TRAILER_TYPE).toString();


                trailerInfoArray[i].setId(id);
                trailerInfoArray[i].setKey(key);
                trailerInfoArray[i].setName(name);
                trailerInfoArray[i].setSite(site);
                trailerInfoArray[i].setSize(size);
                trailerInfoArray[i].setType(type);
            }

            //MovieInfo arr[] = new MovieInfo[AL.size()];dd
            //AL.toArray(arr);
            return trailerInfoArray;
        }
    }








    private class ReviewTask extends AsyncTask<Void,Void,ReviewData[]>{

        private Context mContext;
        ReviewTask(Context context){
            this.mContext=context;
        }

        @Override
        protected ReviewData[] doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String reviewJsonStr = null;



            try {
                String POSTER_BASE_URL = "https://api.themoviedb.org/3/movie/";
                POSTER_BASE_URL+=trailerID;
                POSTER_BASE_URL+="/reviews?";

                final String APP_ID ="api_key";

                Uri uri = Uri.parse(POSTER_BASE_URL).buildUpon()
                        .appendQueryParameter(APP_ID,getString(R.string.api_key)).build();

                URL url = new URL(uri.toString());

                //Log.d("YehiaaaaaaaaaaaaaaaURL=",uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                reviewJsonStr = buffer.toString();
            }

            catch (IOException e){
                return null;
            }

            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {}
                }
            }
            try {
                return getReviewDataFromJson(reviewJsonStr);
            } catch (Exception e) {
            }
            return null;
        }



        @Override
        protected void onPostExecute(ReviewData[] trailerDatas) {

            if(trailerDatas!=null){
                list2 = new ArrayList<>();
                for (ReviewData temp : trailerDatas){
                    list2.add(temp);

                }
                //Log.d("Yehiaaaaaaaaaaaaaaaaaa","el list keda tmam");
            }
            reviewList.setAdapter(new ReviewAdapter(mContext,list2));
        }



        public ReviewData [] getReviewDataFromJson(String trailerJsonStr) {

            final String OWN_LIST ="results";
            final String REVIEW_AUTHOR="author";
            final String REVIEW_CONTENT="content";
            final String REVIRE_URL = "url";

            String author,content,url;

            JSONObject jsonRootObject = null;
            try {
                jsonRootObject = new JSONObject(trailerJsonStr);
            } catch (JSONException e) {
            }

            JSONArray jsonArray = jsonRootObject.optJSONArray(OWN_LIST);
            ReviewData [] reviewInfoArray = new ReviewData[jsonArray.length()];

            for(int i=0;i<jsonArray.length();i++){
                //Log.d("i",String.valueOf(i));
                reviewInfoArray[i] = new ReviewData();

                JSONObject jsonObject = null;
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                } catch (JSONException e) {

                }

                author = jsonObject.optString(REVIEW_AUTHOR).toString();
                content = jsonObject.optString(REVIEW_CONTENT).toString();
                url = jsonObject.optString(REVIRE_URL).toString();

                reviewInfoArray[i].setAuthor(author);
                reviewInfoArray[i].setContent(content);
                reviewInfoArray[i].setUrl(url);
            }
            //MovieInfo arr[] = new MovieInfo[AL.size()];dd
            //AL.toArray(arr);
            return reviewInfoArray;
        }
    }

}
