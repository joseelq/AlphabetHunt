package com.example.AlphabetHunt;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class myCursorAdapter extends CursorAdapter {


    public myCursorAdapter(Context context, Cursor c) {
        super(context, c , 1);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(R.layout.myrow, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView image = (ImageView) view.findViewById(R.id.imv);
        TextView labels = (TextView) view.findViewById(R.id.Text1);
        TextView date = (TextView) view.findViewById(R.id.Text2);
        TextView time = (TextView) view.findViewById(R.id.Text3);
        TextView letter = (TextView) view.findViewById(R.id.Letter);

        byte[] image_bytes = cursor.getBlob(cursor.getColumnIndexOrThrow("photo"));
        Bitmap image_bitmap = BitmapFactory.decodeByteArray(image_bytes, 0, image_bytes.length);
        image.setImageBitmap(image_bitmap);

        String labels_string = cursor.getString(cursor.getColumnIndexOrThrow("labels"));
        labels.setText(labels_string);


        String date_string = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        date.setText(date_string);

        String time_string = cursor.getString(cursor.getColumnIndexOrThrow("time"));
        time.setText(time_string);

        String letter_string = cursor.getString(cursor.getColumnIndexOrThrow("letter"));
        letter.setText(letter_string);

        Log.v("Set Images", "for Letter: " + cursor.getString(cursor.getColumnIndexOrThrow("letter")));


    }
}
