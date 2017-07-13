package com.tommy.driver.socialshare;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class SocialAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<SocialNetwork> images;
    private static int size;

    public SocialAdapter(Context c, ArrayList<SocialNetwork> images, int size) {
        mContext = c;
        this.images = images;
        SocialAdapter.size = size;
    }

    public int getCount() {
        return images.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(size, size));
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(images.get(position).getImage());
        return imageView;
    }

}