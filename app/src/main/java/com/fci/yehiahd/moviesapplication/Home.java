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
    ArrayList<String> list ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        movies_gridView = (GridView) findViewById(R.id.movies_grid_view);
        list = new ArrayList<>();
        //movies_gridView.setAdapter(new GridViewAdapter(this, list));


    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            checkConnection();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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

    public class DownloadPosters extends AsyncTask<Void,Void,String[]>{

        private Context mContext;
        DownloadPosters(Context context){
            this.mContext = context;
        }


        @Override
        protected void onPreExecute() {
            //super.onPreExecute();

        }

        @Override
        protected String[] doInBackground(Void... params) {

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
        protected void onPostExecute(String[] strings) {
            //super.onPostExecute(strings);
            if(strings!=null){

                list.clear();
                for (String temp : strings){
                    list.add(temp);
               }

            }
            movies_gridView.setAdapter(new GridViewAdapter(mContext, list));
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
