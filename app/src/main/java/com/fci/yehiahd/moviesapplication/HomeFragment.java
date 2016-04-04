package com.fci.yehiahd.moviesapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener {


    public HomeFragment() {
        // Required empty public constructor
    }


    GridView movies_gridView;
    ArrayList<MovieInfo> list ;
    int width,height;
    Intent i;
    String prefStatus,oldPrefStatus, tempPref;
    DBConnection db;
    Communicator comm;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getDim();

        if(savedInstanceState!=null){
            list = savedInstanceState.getParcelableArrayList("list");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        movies_gridView = (GridView) rootView.findViewById(R.id.movies_grid_view);
        movies_gridView.setOnItemClickListener(this);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        oldPrefStatus = prefs.getString(getString(R.string.sort_by_key),"");

        if(oldPrefStatus.equals(getString(R.string.favorite_option))){
            getFavoriteMovies();
        }

        else {
            try {
                checkConnection();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        comm = (Communicator) getActivity();

    }


    public void getFavoriteMovies(){
        db = new DBConnection(getActivity());
        list=db.getAllMovies();
        movies_gridView.setAdapter(new GridViewAdapter(getActivity(), list, width, height));

        if(isTablet(getActivity())){
            if(list.size()!=0){
                comm.respond(list.get(0));
            }
            else
                comm.respond(new MovieInfo());
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("list", list);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefStatus = prefs.getString(getString(R.string.sort_by_key), "");

        //Log.d("YehiaPref",prefStatus);
        if(!prefStatus.equals(oldPrefStatus)){
            try {
                if(prefStatus.equals(getString(R.string.favorite_option))){
                    getFavoriteMovies();
                }
                else
                    refresh();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            oldPrefStatus = prefStatus;
        }

        if(!isConnected()&&isTablet(getActivity())){
            try {
                refresh();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void getDim(){
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        if(!isTablet(getActivity())){
            width = (size.x)/2;
            height = (size.y)/2;
        }

        else {
            width = (size.x)/4;
            height = (size.y)/2;
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
                inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

            switch (item.getItemId()){
                case R.id.setting:
                    startActivity(new Intent(getActivity(),SettingActivity.class));
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

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        else
            return false;
    }

    public void refresh() throws ExecutionException, InterruptedException {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        tempPref = prefs.getString(getString(R.string.sort_by_key),"");

        if(tempPref.equals(getString(R.string.favorite_option))){
            getFavoriteMovies();
        }

        else
            checkConnection();
    }

    public void checkConnection() throws ExecutionException, InterruptedException {


        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity()
        .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadPosters(getActivity()).execute();
        }
        else
        {
            if(list.size()!=0){
                movies_gridView.setAdapter(new GridViewAdapter(getActivity(), list, width, height));
                    comm.respond(list.get(0));
            }

            Toast.makeText(getActivity(), "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(!isTablet(getActivity())){
            i = new Intent(getActivity(),MovieDetails.class);
            i.putExtra("film",list.get(position));
            startActivity(i);
        }

        else {
            comm.respond(list.get(position));
        }

    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }



    public class DownloadPosters extends AsyncTask<Void,Void,MovieInfo[]> {

        private Context mContext;
        DownloadPosters(Context context){
            this.mContext = context;
        }



        @Override
        protected MovieInfo[] doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String posterJsonStr = null;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            prefStatus = prefs.getString(getString(R.string.sort_by_key),"");


            try {
                String POSTER_BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?";
                if(prefStatus.equals(getString(R.string.most_pop_option))){
                    POSTER_BASE_URL ="http://api.themoviedb.org/3/movie/popular?";
                }
                final String APP_ID = "api_key";

                Uri uri = Uri.parse(POSTER_BASE_URL).buildUpon()
                        .appendQueryParameter(APP_ID, getString(R.string.api_key)).build();

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
                    //Log.d("tgrobaaaaaaaaaaa",prefStatus);
                }
            }
            movies_gridView.setAdapter(new GridViewAdapter(mContext, list, width, height));
            if(isTablet(getActivity())){
                comm.respond(list.get(0));
            }
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

            StringBuilder path ,backpath;
            String overView ,releaseDate,id,originalTitle,title,backdropPath,voteAverage;
            double popularity;
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
                backpath = new StringBuilder();
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
                backpath.append("http://image.tmdb.org/t/p/").append("w185/").append(jsonObject.optString(BACKDROP_PATH).toString());
                backdropPath = String.valueOf(backpath);
                popularity = jsonObject.optDouble(POPULARITY);
                voteCount = jsonObject.optInt(VOTE_COUNT);
                voteAverage = jsonObject.optString(VOTE_AVERAGE);


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

            //MovieInfo arr[] = new MovieInfo[AL.size()];dd
            //AL.toArray(arr);
            return movieInfosArr;
        }
    }
}
