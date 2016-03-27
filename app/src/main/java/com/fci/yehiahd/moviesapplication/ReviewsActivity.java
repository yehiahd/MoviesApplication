package com.fci.yehiahd.moviesapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ReviewsActivity extends AppCompatActivity {

    ListView reviewList;
    ArrayList<ReviewData> list;
    MovieInfo movieInfo;
    String trailerID;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        setTitle("Reviews :");

        movieInfo = getIntent().getExtras().getParcelable("object");
        trailerID = movieInfo.getId();
        reviewList = (ListView) findViewById(R.id.detail_reviews_list_view);
        new ReviewTask(this).execute();

    }



    private class ReviewTask extends AsyncTask<Void,Void,ReviewData[]> {

        private Context mContext;
        ReviewTask(Context context){
            this.mContext=context;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(ReviewsActivity.this,"Yehia","Loading...",true);
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
                list = new ArrayList<>();
                for (ReviewData temp : trailerDatas){
                    list.add(temp);

                }
                //Log.d("Yehiaaaaaaaaaaaaaaaaaa","el list keda tmam");
            }
            reviewList.setAdapter(new ReviewAdapter(mContext,list));
            dialog.cancel();
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
