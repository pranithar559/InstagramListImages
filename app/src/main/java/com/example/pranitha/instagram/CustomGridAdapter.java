package com.example.pranitha.instagram;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pranitha on 11-12-14.
 */
//here we are defining the custom grid adapter for the grid view
public class CustomGridAdapter extends SimpleAdapter {
    private Context mContext;
    public LayoutInflater inflater = null;
    public CustomGridAdapter(Context context,
                           List<? extends Map<String, ?>> data, int resource, String[] from,
                           int[] to) {
        super(context, data, resource, from, to);
        mContext = context;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.list_row, null);

        HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
//here for loading every grid view item, we call an async task where the image url is loaded into imageview
        new DownloadTask((ImageView) vi.findViewById(R.id.image))
                .execute((String) data.get("url"));

        return vi;
    }

}