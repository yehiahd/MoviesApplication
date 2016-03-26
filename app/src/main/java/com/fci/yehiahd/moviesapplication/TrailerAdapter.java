package com.fci.yehiahd.moviesapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by yehia on 25/03/16.
 */

public class TrailerAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<TrailerData> list;


    public TrailerAdapter(Context mContext,ArrayList<TrailerData> list){
        this.mContext= mContext;
        this.list = new ArrayList<>(list);
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

        View v = convertView;

        if(v==null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v =inflater.inflate(R.layout.trailers_row,parent,false);
            //Log.d("Yehia","Null");
        }

        TextView trailerTitle = (TextView) v.findViewById(R.id.trailer_row_text_view);
        ImageView imageView = (ImageView) v.findViewById(R.id.trailer_row_image_view);

        TrailerData row =list.get(position);

        trailerTitle.setText(row.getName());
        imageView.setImageResource(R.drawable.play);

        return v;
    }
}
