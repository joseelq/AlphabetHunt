package com.example.AlphabetHunt;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
//import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class History extends AppCompatActivity {
    photoDatabase database;
    BarChart chart;
    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    double[] times = new double[26];
    BarData barData;
    BarDataSet barDataSet;
    SQLiteDatabase sqLiteDatabase;
    NestedScrollView sl;

    AlertDialog alertDialog;

    // RECENT PHOTOS
    ListView listView = null;

    String[] letters = new String[] {"A", "B", "C" ,"D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
                                      "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    LinearLayout parent;
    RelativeLayout parentParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_screen);
        parent = (LinearLayout) findViewById(R.id.parent);
        parentParent = findViewById(R.id.LParent);
        sl = new NestedScrollView(this);

  //      linearLayout = new LinearLayout(this);

       chart = (BarChart) findViewById(R.id.Bar);

        database = new photoDatabase(this);
        sqLiteDatabase = database.getReadableDatabase();


         bar();

        for (int i=0; i< letters.length; i++){
            ListView listView = new ListView(this);
            Cursor cursor =database.getReadableDatabase().rawQuery(
                    "SELECT _id, photo as photo, letter as letter, labels as labels, date as date, time as time" +
                            " FROM Photos" +
                            " WHERE letter " +
                            "LIKE '%"+ letters[i]+"%'", null);

            myCursorAdapter myCursorAdapter = new myCursorAdapter(this, cursor);

            if (cursor.getCount() == 0){

                continue;
            }
            listView.setAdapter(myCursorAdapter);
            parent.addView(listView);


        }
     //   parentParent.addView(parent);


    }

    // CREATE A LIST VIEW WITH THE NECESSARY IMAGES AND THEIR CORRESPONDING DATA

/*    public void displayImages(){
        ArrayList<byte[]> image_images = null;
        ArrayList<String> labels = null;
        ArrayList<String> dates = null;
        ArrayList<String> image_times = null;


        for (int i = 0; i < 26; i++){
            byte[] e = null;

            Cursor c = database.getReadableDatabase().rawQuery(
                    "SELECT photo as photo, labels as labels, date as date, time as time" +
                            " FROM Photos" +
                            " WHERE letter " +
                            "LIKE '%" + letters[i] + "%'", null);
            if (c != null){
                c.moveToFirst();
                //DECLARE ARRAY SIZE DEPENDING ON HOW MANY IMAGES THERE ARE
                image_images = new ArrayList<byte[]>();
                        //byte[1][c.getCount()];
                labels = new ArrayList<>();
                dates = new ArrayList<>();
                image_times = new ArrayList<>();
                for (int j = 0; j < c.getCount(); j++ ){
                    //POPULATE ARRAY
                    image_images.add(c.getBlob(c.getColumnIndexOrThrow("photo")));
                    labels.add(c.getString(c.getColumnIndexOrThrow("labels")));
                    dates.add(c.getString(c.getColumnIndexOrThrow("date")));
                    image_times.add(c.getString(c.getColumnIndexOrThrow("time")));
                    Log.v("Inside Cursor", "Letter" + letters[i]);
                    if (letters[i] == "E"){
                        e = c.getBlob(c.getColumnIndexOrThrow("photo"));
                    }
                    c.moveToNext();
                }


            } else {
                Log.v("No Images Exist", "Letter " + letters[i] + " has not images.");
            }
            int image_count = 0;


            for (int k = 0; k <image_images.size(); k++){
                if (image_count == 0){
                    View v = getLayoutInflater().inflate(R.layout.history_screen, null);
                    v.setId(i);

                    LinearLayout parent = v.findViewById(R.id.parentLinear);
                    parent.setId(k);

                    ImageView displayImage = (ImageView) parent.findViewById(R.id.imv1);
                    Bitmap b = BitmapFactory.decodeByteArray(image_images.get(k), 0, image_images.get(k).length);
                    //displayImage.setId(k);
                    displayImage.setImageBitmap(b);

                    TextView labelsText = (TextView) v.findViewById(R.id.Text11);
                    labelsText.setText(labels.get(k));

                    TextView dateText = (TextView) v.findViewById(R.id.Text12);
                    dateText.setText(dates.get(k));

                    TextView timeText = (TextView) v.findViewById(R.id.Text13);
                    timeText.setText(image_times.get(k));

                    Log.v("Inside Image == 0", "Letter " + letters[i]);
                    Log.v("Labels", ": " + labels.get(k));


                    linearLayout.addView(v);
                    image_count++;

                }
                if (image_count == 1){
                    View v = getLayoutInflater().inflate(R.layout.history_screen, null);

                    LinearLayout linearLayout1 = v.findViewById(R.id.parentLinear);
                    linearLayout1.setId(i);

                    ImageView displayImage = (ImageView) v.findViewById(R.id.imv2);
                    Bitmap b = BitmapFactory.decodeByteArray(image_images.get(k), 0, image_images.get(k).length);
                    displayImage.setImageBitmap(b);

                    TextView labelsText = (TextView) v.findViewById(R.id.Text21);
                    labelsText.setText(labels.get(k));

                    TextView dateText = (TextView) v.findViewById(R.id.Text22);
                    dateText.setText(dates.get(k));

                    TextView timeText = (TextView) v.findViewById(R.id.Text23);
                    timeText.setText(image_times.get(k));

                    linearLayout.addView(v);
                    image_count++;


                }
                if (image_count == 2){
                    View v = getLayoutInflater().inflate(R.layout.history_screen, null);

                    LinearLayout linearLayout1 = v.findViewById(R.id.parentLinear);
                    linearLayout1.setId(i);

                    ImageView displayImage = (ImageView) v.findViewById(R.id.imv3);
                    Bitmap b = BitmapFactory.decodeByteArray(image_images.get(k), 0, image_images.get(k).length);
                    displayImage.setImageBitmap(b);

                    TextView labelsText = (TextView) v.findViewById(R.id.Text31);
                    labelsText.setText(labels.get(k));

                    TextView dateText = (TextView) v.findViewById(R.id.Text32);
                    dateText.setText(dates.get(k));

                    TextView timeText = (TextView) v.findViewById(R.id.Text33);
                    timeText.setText(image_times.get(k));

                    linearLayout.addView(v);

                }


            }


        }






    }*/











    public void bar(){
        for (int i=0; i<26; i++){

            try {
                String query = "SELECT AVG(" + "time" + ") as average FROM Photos WHERE letter LIKE ? ";
                Cursor c = database.getReadableDatabase().rawQuery(
                        "SELECT AVG("+"time" +") as average" +
                                " FROM Photos" +
                                " WHERE letter " +
                                "LIKE '%"+letters[i]+"%'", null);

                //'%"+filter+"%'"
                c.moveToFirst();





                long time_passed = c.getLong(0);
              /*  double h   = (double) (time_passed /3600000);
                double m = (double) (time_passed - h*3600000)/60000;
                double s= (double) (time_passed - h*3600000- m*60000)/1000 ;
*/


               double hours = (double) (time_passed / 3600000);
                double minutes = (time_passed - hours * 3600000) / 60000;
                double seconds = (time_passed - hours*3600000-minutes*60000)/1000;
                times[i] = minutes;

                Log.v("Letter has Photo",alphabet.charAt(i) + "" );


            } catch (SQLException e){
                Log.v("No Photos", "");
                times[i] = Long.valueOf(0);
            }


        }

        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i=0; i < times.length; i++){
            char x = alphabet.charAt(i);
            double y = times[i];
            values.add(new BarEntry(x, (float) y));
        }
        BarDataSet barDataSet = new BarDataSet(values, "Seconds");


        ArrayList<String> xAxisLabel = new ArrayList<>();
        for (int i =0; i< letters.length; i++){
            xAxisLabel.add(letters[i]);
        }


        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setDrawGridLines(false);
        Log.v("Labels count", "Count: " + xAxis.getLabelCount());


       chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(letters));
       chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setGranularityEnabled(true);


        YAxis leftAxis = chart.getAxisLeft();
      //  leftAxis.setAxisMinimum(0f);
      //  chart.getAxisRight().setEnabled(false); // no right axis



       barDataSet = new BarDataSet(values, "Letters");
       barData = new BarData(barDataSet);
       barData.setBarWidth(0.5f);
        chart.setData(barData);
        chart.setFitBars(true);
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);

       // barDataSet.setValueTextSize(18f);

       // linearLayout.addView(chart);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int x = chart.getData().getDataSetForEntry(e).getEntryIndex((BarEntry) e);
                String letta = xAxisLabel.get(x);
                AlertDialog.Builder builder = new AlertDialog.Builder(History.this);
                builder.setCancelable(true);
                View letterView = LayoutInflater.from(History.this).inflate(R.layout.barletter, null);
                TextView letterText= (TextView) letterView.findViewById(R.id.Letter);
                letterText.setText(letta);
                builder.setView(letterView);
                alertDialog = builder.create();
                alertDialog.show();


            }

            @Override
            public void onNothingSelected() {

            }
        });


    }



}
