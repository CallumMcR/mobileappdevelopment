package com.example.calendarapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "calendardb";
    private static final int DATABASE_VERSION = 7;

    private static final String TABLE_EVENTS = "events";
    private static final String TABLE_PICTURES = "pictures";

    private static final String ID_COL = "eventID";
    private static final String DESC_COL = "eventDescription";
    private static final String DATETIME_COL = "dateTimeJava";

    private static final String PICTURE_ID_COL = "pictureID";
    private static final String PICTURE_DATA_COL = "pictureData";
    private static final String PICTURE_DATE_COL = "pictureDate";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String eventTableQuery = "CREATE TABLE " + TABLE_EVENTS + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DESC_COL + " TEXT, "
                + DATETIME_COL + " DATETIME)";
        db.execSQL(eventTableQuery);

        String pictureTableQuery = "CREATE TABLE " + TABLE_PICTURES + " ("
                + PICTURE_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PICTURE_DATA_COL + " BLOB, "
                + PICTURE_DATE_COL + " DATETIME)";
        db.execSQL(pictureTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICTURES);
        onCreate(db);
    }

    public void addNewEvent(String description, String eventDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date setDate = dateFormat.parse(eventDate);
            values.put(DESC_COL, description);
            values.put(DATETIME_COL, eventDate);

            db.insert(TABLE_EVENTS, null, values);
            db.close();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Cursor getEvents(String date) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + DATETIME_COL +
                " LIKE '%" + date + "%' ORDER BY " + DATETIME_COL + " ASC";
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public void removeEvent(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_EVENTS, ID_COL + "=?", new String[]{id});
        db.close();
    }

    public void addPhoto(byte[] photoData, String photoDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PICTURE_DATA_COL, photoData);
        values.put(PICTURE_DATE_COL, photoDate);

        db.insert(TABLE_PICTURES, null, values);
        Log.d("Insertion", values.toString());
    }


    public Cursor getPhotos(String date) {
            SQLiteDatabase db = getWritableDatabase();
            String query = "SELECT * FROM " + TABLE_PICTURES + " WHERE " + PICTURE_DATE_COL +
                    " LIKE '%" + date + "%' ORDER BY " + PICTURE_DATE_COL + " ASC";
            Cursor cursor = db.rawQuery(query, null);
            return cursor;
    }





    public void deletePhoto(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PICTURES, PICTURE_ID_COL + "=?", new String[]{id});
        db.close();
    }
}
