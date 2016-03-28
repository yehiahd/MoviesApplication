package com.fci.yehiahd.moviesapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by yehia on 28/03/16.
 */
public class DBConnection extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DBName = "favorites.db";

    public DBConnection(Context context) {

        super(context, DBName, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table IF NOT EXISTS favorites (id TEXT primary key,url TEXT,date TEXT,rate TEXT,description TEXT,title TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if EXISTS favorites");
        onCreate(db);
    }

    public void addMovie(String id ,String url,String date,String rate , String description,String title){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);
        contentValues.put("url",url);
        contentValues.put("date",date);
        contentValues.put("rate",rate);
        contentValues.put("description", description);
        contentValues.put("title", title);

        db.insert("favorites", null, contentValues);
    }

    public ArrayList<MovieInfo> getAllMovies(){

        int i=0;
        String res_id,res_url,res_date,res_rate,res_description,res_title;
        MovieInfo movieInfo[] = new MovieInfo[getMoviesCount()];
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from favorites",null);
        res.moveToFirst();
        //Log.d("Size l array kollo " , String.valueOf(movieInfo.length));

        while (!res.isAfterLast()){
            res_id =res.getString(res.getColumnIndex("id"));
            res_url = res.getString(res.getColumnIndex("url"));
            res_date = res.getString(res.getColumnIndex("date"));
            res_rate  = res.getString(res.getColumnIndex("rate"));
            res_description = res.getString(res.getColumnIndex("description"));
            res_title = res.getString(res.getColumnIndex("title"));
            if(i==movieInfo.length)
                break;

            movieInfo[i] = new MovieInfo(res_url,res_description,res_date,res_id,"",res_title,"",0.0,0,res_rate);
            res.moveToNext();
            i++;

            //Log.d("Yehiaaaaaaaaaaaaaaa",""+i);
        }

        return new ArrayList<MovieInfo>(Arrays.asList(movieInfo));
    }

    public int getMoviesCount() {
        String countQuery = "SELECT * FROM favorites" ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }


    public boolean checkID(String id){
        String query = "SELECT * FROM favorites WHERE id="+id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.getCount()==0)
            return true;
        else return removeMovie(id);
    }

    public boolean removeMovie(String id){
        String query = "DELETE FROM favorites WHERE id="+id;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        return false;
    }

    public boolean isFavorite(String id){
        String query = "SELECT * FROM favorites WHERE id="+id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.getCount()!=0)
            return true;
        else
            return false;
    }
}
