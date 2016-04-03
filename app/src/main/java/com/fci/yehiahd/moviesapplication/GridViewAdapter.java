package com.fci.yehiahd.moviesapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by yehia on 19/03/16.
 */
public class GridViewAdapter extends BaseAdapter {

   /* int temp[] = {R.drawable.spartacus,R.drawable.game_of_thrones,R.drawable.avatar,R.drawable.avengers,R.drawable.blackswan
    ,R.drawable.cap_america,R.drawable.darkknight,R.drawable.divergent,R.drawable.gods_of_egypt
    ,R.drawable.interstellar,R.drawable.ironman2,R.drawable.lightning_thief,R.drawable.looper
    ,R.drawable.realsteal,R.drawable.twilight,R.drawable.jab_tak_hai_jaan};*/
    Context mContext;
    ArrayList<MovieInfo> list;
    ImageView imageView;
    int width,height;

    GridViewAdapter(Context context,ArrayList<MovieInfo> list , int width, int height){
        this.mContext = context;
        this.list = new ArrayList<>(list);
        this.width=width;
        this.height=height;
    }


    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

         imageView = (ImageView) convertView;

        if(imageView==null){

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v =inflater.inflate(R.layout.sample,parent,false);
            imageView = (ImageView) v.findViewById(R.id.sample_id);
        }
        //Log.d("Yehia", String.valueOf(list.get(position)) + " " + String.valueOf(getCount()));
        //Toast.makeText(mContext, ""+getCount(), Toast.LENGTH_SHORT).show();
        Picasso.with(mContext).load(list.get(position).getPoster_path()).placeholder(R.drawable.icon_loading).resize(this.width,this.height).into(imageView);//360,512
        //Log.d("Vote Avg of movie "+position , String.valueOf(list.get(position).getVote_average()));
        return imageView;
    }
}