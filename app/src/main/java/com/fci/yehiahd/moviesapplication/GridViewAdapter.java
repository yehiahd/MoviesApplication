package com.fci.yehiahd.moviesapplication;

import android.content.Context;
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

    /*int temp[] = {R.drawable.spartacus,R.drawable.game_of_thrones,R.drawable.avatar,R.drawable.avengers,R.drawable.blackswan
    ,R.drawable.cap_america,R.drawable.darkknight,R.drawable.divergent,R.drawable.gods_of_egypt
    ,R.drawable.interstellar,R.drawable.ironman2,R.drawable.lightning_thief,R.drawable.looper
    ,R.drawable.realsteal,R.drawable.twilight,R.drawable.jab_tak_hai_jaan};
*/
    Context mContext;
    ArrayList<String> list;

    GridViewAdapter(Context context,ArrayList<String> list){
        this.mContext = context;
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView view = (ImageView) convertView;

        if(view==null){
            view = new ImageView(mContext);
        }
        Picasso.with(mContext).load(String.valueOf(list.get(position))).resize(360, 512).into(view);
        return view;
    }
}
