package com.example.AlphabetHunt;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class photoDatabase extends SQLiteOpenHelper {
    SQLiteDatabase sqLiteDatabase;
    private static final String PHOTOS = "Photos";
    private static final String PHOTO = "photo";
    private static final String LABELS = "labels";
    private static final String TIME = "time";
    private static final String DATE = "date";
    private static final String LETTER = "letter";
    private static final String ID = "_id";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + PHOTOS + " ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+PHOTO+" TEXT, "  +LETTER+ " TEXT, " +LABELS+ " TEXT, " + DATE+ " TEXT, "+ TIME+" TEXT);";
    private Context context;
    public photoDatabase(Context context) {
        super(context, PHOTOS, null, 1);
        context = this.context;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PHOTOS);
            onCreate(sqLiteDatabase);
    }

    public boolean addPhoto(byte[] photo, String labels, String letter, String date, long time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("photo", photo);
        values.put("letter", letter+"");
        values.put("labels", labels);
        values.put("date", date);
        values.put("time",  time);

        long result = db.insert(PHOTOS, null, values);
        if (result == -1) {
            Log.v("Didn't Add Row ", "Letter: " + letter);
            return false;
        } else {
            Log.v("Added Row Successfully ", "Letter: " + letter);
            return true;
        }
    }
}
