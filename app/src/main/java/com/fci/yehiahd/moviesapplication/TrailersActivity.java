package com.fci.yehiahd.moviesapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

public class TrailersActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    MovieInfo movieInfo;
    String trailerID;
    ArrayList<TrailerData> list ;
    ListView trailerList;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailers);
        setTitle("Trailers :");

        movieInfo = getIntent().getExtras().getParcelable("object");
        trailerID = movieInfo.getId();
        trailerList = (ListView) findViewById(R.id.detail_trailer_list_view);
        trailerList.setOnItemClickListener(this);
        new TrailerTask(this).execute();

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).getKey())));
    }

    private class TrailerTask extends AsyncTask<Void,Void,TrailerData[]> {

        private Context mContext;
        TrailerTask(Context context){
            this.mContext=context;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(TrailersActivity.this,"Yehia","Loading...",true);
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
            dialog.cancel();
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
}
