package com.example.msk.mapsample.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.example.msk.mapsample.Model.Post;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void insertPost(String comment, String image, String location, double latitude, double longitude, String postDate, String updatedDate){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO POST VALUES(NULL, ?, ?, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, comment);
        statement.bindString(2, image);
        statement.bindString(3, location);
        statement.bindDouble(4, latitude);
        statement.bindDouble(5, longitude);
        statement.bindString(6, postDate);
        statement.bindString(7, updatedDate);

        statement.executeInsert();
    }

    public void updatePost(String comment, String image, String updatedDate, int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE POST SET comment=?, image=?, updatedDate=? WHERE id=?";

        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1, comment);
        statement.bindString(2, image);
        statement.bindString(3, updatedDate);
        statement.bindDouble(4, (double)id);

        statement.execute();
        database.close();
    }

    public void deletePost(int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM POST WHERE id=?";

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

    public List<Post> search(String searchTerm) {
        SQLiteDatabase database = getReadableDatabase();
        String sql = "SELECT  id, " +
                " comment, " +
                " image, " +
                " location, " +
                " latitude, " +
                " longitude, " +
                " postDate, " +
                " updatedDate " +
                " FROM POST " +
                " WHERE comment  LIKE '%" + searchTerm + "%' OR" +
                "       location LIKE '%" + searchTerm + "%' OR" +
                "       postDate LIKE '%" + searchTerm + "%' OR" +
                "       updatedDate LIKE '%" + searchTerm + "%'";

        // execute the sql statement
        Cursor cursor = database.rawQuery(sql, null);

        ArrayList<Post> posts = new ArrayList<>();

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String comment = cursor.getString(1);
                String image = cursor.getString(2);
                String location = cursor.getString(3);
                double latitude = cursor.getDouble(4);
                double longitude = cursor.getDouble(5);
                String postDate = cursor.getString(6);
                String updatedDate = cursor.getString(7);
                posts.add(new Post(id, comment, image, location, latitude, longitude, postDate, updatedDate));
            }
        }
        cursor.close();
        return posts;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
