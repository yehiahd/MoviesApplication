package com.fci.yehiahd.moviesapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by yehia on 26/03/16.
 */
public class ReviewAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<ReviewData> list;
    View v2;

    public ReviewAdapter(Context context , ArrayList<ReviewData> list){
        this.mContext=context;
        this.list= new ArrayList<>(list);
    }
    @Override
    public int getCount() {
        if(list.size()==0)
            return 1;
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

        if(list.size()==0){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v2 =inflater.inflate(R.layout.reviews_row,parent,false);

            TextView reviewAuthor2 = (TextView) v2.findViewById(R.id.review_row_text_view_author);
            TextView reviewContent2 = (TextView) v2.findViewById(R.id.review_row_text_view_content);
            reviewAuthor2.setText("");
            reviewContent2.setText("No reviews to show!");
            return v2;
        }

        if(v==null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v =inflater.inflate(R.layout.reviews_row,parent,false);
        }

        TextView reviewAuthor = (TextView) v.findViewById(R.id.review_row_text_view_author);
        TextView reviewContent = (TextView) v.findViewById(R.id.review_row_text_view_content);

        ReviewData row =list.get(position);

        reviewAuthor.setText(row.getAuthor());
        reviewContent.setText(row.getContent());

        return v;
    }
}
