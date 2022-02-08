package com.example.AlphabetHunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class myArrayAdapter extends ArrayAdapter<String> {
    byte[][] images;
    String[] labels;
    String[] times;
    String[] dates;
    Context context;
    int count;

    public myArrayAdapter(Context context, byte[][] images, String[] labels, String[] times, String[] dates, int count) {
        super(context, R.layout.myrow);
        this.images = images;
        this.labels = labels;
        this.times = times;
        this.dates = dates;
        this.count = count;
        this.context = context;
    }

    public int getCount(int position){
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent){
       ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInFlater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInFlater.inflate(R.layout.myrow, parent, false);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.imv);
            viewHolder.labels = (TextView) convertView.findViewById(R.id.Text1);
            viewHolder.date = (TextView) convertView.findViewById(R.id.Text2);
            viewHolder.time= (TextView) convertView.findViewById(R.id.Text3);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

            byte[] image = images[position];
            Bitmap b = BitmapFactory.decodeByteArray(images[position], 0, images[position].length);


            viewHolder.image.setImageBitmap(b);
            viewHolder.labels.setText(labels[position]);
            viewHolder.date.setText(dates[position]);
            viewHolder.time.setText(times[position]);

            return convertView;


    }

    static class ViewHolder {
        ImageView image;
        TextView labels;
        TextView date;
        TextView time;
    }
}


