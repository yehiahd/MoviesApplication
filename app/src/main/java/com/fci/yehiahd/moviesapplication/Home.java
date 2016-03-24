package com.fci.yehiahd.moviesapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

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
import java.util.concurrent.ExecutionException;

public class Home extends AppCompatActivity {

    GridView movies_gridView;
    ArrayList<MovieInfo> list ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        movies_gridView = (GridView) findViewById(R.id.movies_grid_view);
        //movies_gridView.setAdapter(new GridViewAdapter(this, list));

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            checkConnection();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.setting:
                startActivity(new Intent(Home.this,SettingActivity.class));
                break;
            case R.id.refresh:
                try {
                    refresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refresh() throws ExecutionException, InterruptedException {
        checkConnection();
    }

    public void checkConnection() throws ExecutionException, InterruptedException {

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadPosters(this).execute();
        }
        else
        {
            Toast.makeText(Home.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

        }
    }

    public class DownloadPosters extends AsyncTask<Void,Void,MovieInfo[]>{

        private Context mContext;
        DownloadPosters(Context context){
            this.mContext = context;
        }


        @Override
        protected void onPreExecute() {
            //super.onPreExecute();

        }

        @Override
        protected MovieInfo[] doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String posterJsonStr = null;


            try {
                final String POSTER_BASE_URL = "http://api.themoviedb.org/3/movie/popular?";
                final String APP_ID = "api_key";

                Uri uri = Uri.parse(POSTER_BASE_URL).buildUpon()
                        .appendQueryParameter(APP_ID, "e39a604199763f197c27e4c919161bcf").build();

                URL url = new URL(uri.toString());

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
                posterJsonStr = buffer.toString();
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
                return getPosterPathFromJson(posterJsonStr);
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieInfo[] strings) {
            //super.onPostExecute(strings);
            if(strings!=null){

                list = new ArrayList<>();
                for (MovieInfo temp : strings){
                    list.add(temp);
               }

            }
           movies_gridView.setAdapter(new GridViewAdapter(mContext, list));

        }


        public MovieInfo [] getPosterPathFromJson(String posterJsonStr) {

            final String POSTER_PATH="poster_path";
            final String OWN_LIST ="results";
            final String OVER_VIEW="overview";
            final String RELESE_DATE = "release_date";
            final String ID = "id";
            final String ORIGINAL_TITLE="original_title";
            final String TITLE ="title";
            final String BACKDROP_PATH ="backdrop_path";
            final String POPULARITY ="popularity";
            final String VOTE_COUNT = "vote_count";
            final String VOTE_AVERAGE= "vote_average";

            StringBuilder path ;
            String overView ,releaseDate,id,originalTitle,title,backdropPath;
            double popularity,voteAverage;
            int voteCount;

            JSONObject jsonRootObject = null;
            try {
                jsonRootObject = new JSONObject(posterJsonStr);
            } catch (JSONException e) {
            }

            JSONArray jsonArray = jsonRootObject.optJSONArray(OWN_LIST);
            MovieInfo [] movieInfosArr = new MovieInfo[jsonArray.length()];
            //Log.d("7gm l array", String.valueOf(movieInfosArr.length));

            for(int i=0;i<jsonArray.length();i++){
                //Log.d("i",String.valueOf(i));
                path = new StringBuilder();
                movieInfosArr[i] = new MovieInfo();

                JSONObject jsonObject = null;
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                } catch (JSONException e) {

                }

                path.append("http://image.tmdb.org/t/p/").append("w185/").append(jsonObject.optString(POSTER_PATH).toString());
                overView = jsonObject.optString(OVER_VIEW).toString();
                releaseDate = jsonObject.optString(RELESE_DATE).toString();
                id = jsonObject.optString(ID).toString();
                originalTitle = jsonObject.optString(ORIGINAL_TITLE).toString();
                title = jsonObject.optString(TITLE).toString();
                backdropPath = jsonObject.optString(BACKDROP_PATH).toString();
                popularity = jsonObject.optDouble(POPULARITY);
                voteCount = jsonObject.optInt(VOTE_COUNT);
                voteAverage = jsonObject.optDouble(VOTE_AVERAGE);


                movieInfosArr[i].setPoster_path(String.valueOf(path));
                movieInfosArr[i].setOverview(overView);
                movieInfosArr[i].setRelease_date(releaseDate);
                movieInfosArr[i].setId(id);
                movieInfosArr[i].setOriginal_title(originalTitle);
                movieInfosArr[i].setTitle(title);
                movieInfosArr[i].setBackdrop_path(backdropPath);
                movieInfosArr[i].setPopularity(popularity);
                movieInfosArr[i].setVote_count(voteCount);
                movieInfosArr[i].setVote_average(voteAverage);
            }

            //MovieInfo arr[] = new MovieInfo[AL.size()];
            //AL.toArray(arr);
            return movieInfosArr;
        }
    }

}
