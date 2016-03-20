package com.fci.yehiahd.moviesapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class Home extends AppCompatActivity {

    GridView movies_gridView;
    ProgressDialog dialog;
    ArrayList<String> list ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        movies_gridView = (GridView) findViewById(R.id.movies_grid_view);
        list = new ArrayList();
        checkConnection();
       // movies_gridView.setAdapter(new GridViewAdapter(this, list));

        //Picasso.with(this).load("http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg").into(movies_gridView);
    }


    public void checkConnection(){

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

    public class DownloadPosters extends AsyncTask<Void,Void,String[]>{

        private Context mContext;
        DownloadPosters(Context context){
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog = ProgressDialog.show(mContext, "Yehia", "Loading...", true);
        }

        @Override
        protected String[] doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String posterJsonStr = null;
           // Toast.makeText(getApplicationContext() , "eh b2a ?", Toast.LENGTH_SHORT).show();


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
                    // Nothing to do.
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
                    } catch (final IOException e) {
                        //Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_SHORT).show();

                    }
                }
            }
            try {
                //Toast.makeText(getApplicationContext(), "it Works", Toast.LENGTH_SHORT).show();
                return getPosterPathFromJson(posterJsonStr);
            } catch (Exception e) {
                //Toast.makeText(getApplicationContext(), "3", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            int i=0;
            super.onPostExecute(strings);
            if(strings!=null){

                list.clear();
                for (String temp : strings){
                    list.add(temp);
               }

            }
            movies_gridView.setAdapter(new GridViewAdapter(mContext, list));
            //dialog.cancel();
        }


        public String [] getPosterPathFromJson(String posterJsonStr) {

            final String PATH="poster_path";
            final String OWN_LIST ="results";
            StringBuilder path ;

            ArrayList<String> AL = new ArrayList<>();

            JSONObject jsonRootObject = null;
            try {
                jsonRootObject = new JSONObject(posterJsonStr);
            } catch (JSONException e) {
            }
            JSONArray jsonArray = jsonRootObject.optJSONArray(OWN_LIST);

            for(int i=0;i<jsonArray.length();i++){
                path = new StringBuilder();
                JSONObject jsonObject = null;
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                } catch (JSONException e) {

                }
                path.append("http://image.tmdb.org/t/p/").append("w185/").append(jsonObject.optString(PATH).toString());

                AL.add(String.valueOf(path));
            }

            String arr[] = new String[AL.size()];
            AL.toArray(arr);
            return arr;
        }


    }


}
