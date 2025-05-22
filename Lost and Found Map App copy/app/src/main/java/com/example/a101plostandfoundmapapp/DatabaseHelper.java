package com.example.a101plostandfoundmapapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LostFound.db";
    private static final int DATABASE_VERSION = 2; // Bump version to force recreation

    public static final String TABLE_NAME = "advert";
    public static final String COL_ID = "id";
    public static final String COL_TYPE = "type";
    public static final String COL_NAME = "name";
    public static final String COL_PHONE = "phone";
    public static final String COL_DESC = "description";
    public static final String COL_DATE = "date";
    public static final String COL_LOCATION = "location";
    public static final String COL_LATITUDE = "latitude";
    public static final String COL_LONGITUDE = "longitude";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TYPE + " TEXT, " +
                COL_NAME + " TEXT, " +
                COL_PHONE + " TEXT, " +
                COL_DESC + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_LOCATION + " TEXT, " +
                COL_LATITUDE + " REAL, " +
                COL_LONGITUDE + " REAL)";
        db.execSQL(createTable);
    }

    // Drop and recreate
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert new advert
    public boolean insertAdvert(String type, String name, String phone, String description, String date, String location, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TYPE, type);
        values.put(COL_NAME, name);
        values.put(COL_PHONE, phone);
        values.put(COL_DESC, description);
        values.put(COL_DATE, date);
        values.put(COL_LOCATION, location);
        values.put(COL_LATITUDE, latitude);
        values.put(COL_LONGITUDE, longitude);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    // Fetch all adverts
    public Cursor getAllAdverts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    // Delete by ID
    public boolean deleteAdvert(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
        return deletedRows > 0;
    }

    // Fetch item by ID
    public Cursor getAdvertById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_ID + " = ?", new String[]{String.valueOf(id)});
    }
}
