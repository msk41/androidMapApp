package com.example.msk.mapsample.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void insertPost(String comment, byte[] image, String location, double latitude, double longitude, String postDate, String updatedDate){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO POST VALUES(NULL, ?, ?, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, comment);
        statement.bindBlob(2, image);
        statement.bindString(3, location);
        statement.bindDouble(4, latitude);
        statement.bindDouble(5, longitude);
        statement.bindString(6, postDate);
        statement.bindString(7, updatedDate);

        statement.executeInsert();
    }

    public void updatePost(String comment, byte[] image, String updatedDate, int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE RECORD SET comment=?, image=?, updatedDate=? WHERE id=?";

        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1, comment);
        statement.bindBlob(2, image);
        statement.bindString(3, updatedDate);
        statement.bindDouble(4, (double)id);

        statement.execute();
        database.close();
    }

    public void deletePost(int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM RECORD WHERE id=?";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double)id);

        statement.execute();
        database.close();
    }

    public Cursor getPost(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
