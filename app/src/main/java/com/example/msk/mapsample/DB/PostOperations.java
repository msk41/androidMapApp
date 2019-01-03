package com.example.msk.mapsample.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.msk.mapsample.Model.Post;

import java.util.ArrayList;
import java.util.List;

public class PostOperations {

    public static final String LOG_TAG = "POST_MANAGEMENT";

    private SQLiteOpenHelper dbHandler;
    private SQLiteDatabase   database;

    private static final String[] allColumns = {
            PostDBHandler.COLUMN_ID,
            PostDBHandler.COLUMN_COMMENT,
            PostDBHandler.COLUMN_IMAGE,
            PostDBHandler.COLUMN_LOCATION,
            PostDBHandler.COLUMN_POST_DATE,
            PostDBHandler.COLUMN_UPDATE_DATE
    };

    public PostOperations(Context context) {
        dbHandler = new PostDBHandler(context);
    }

    public Post addPost(Post post) {

        open();
        ContentValues values = new ContentValues();
        values.put(PostDBHandler.COLUMN_COMMENT, post.getComment());
        values.put(PostDBHandler.COLUMN_IMAGE, post.getImage());
        values.put(PostDBHandler.COLUMN_LOCATION, post.getLocation());
        values.put(PostDBHandler.COLUMN_POST_DATE, post.getPostDate());
        values.put(PostDBHandler.COLUMN_UPDATE_DATE, post.getUpdatedDate());

        long insertId = database.insert(PostDBHandler.TABLE_NAME, null, values);
        post.setPostId(insertId);
        close();
        return post;
    }

    public Post getPost(long id) {


        database = dbHandler.getReadableDatabase();
        Cursor cursor = database.query(PostDBHandler.TABLE_NAME,
                allColumns,
                PostDBHandler.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Post post = new Post(Long.parseLong(cursor.getString(0)),
                                            cursor.getString(1),
                                            cursor.getBlob(2),
                                            cursor.getString(3),
                                            cursor.getString(4),
                                            cursor.getString(5));
        close();
        return post;
    }

    public List<Post> getAllPost() {

        database = dbHandler.getReadableDatabase();
        Cursor cursor = database.query(PostDBHandler.TABLE_NAME,
                allColumns,
                null,
                null,
                null,
                null,
                null);

        List<Post> posts = new ArrayList<>();
        if (cursor.getCount() > 0) {

            while(cursor.moveToNext()) {

                Post post = new Post();

                post.setPostId(cursor.getLong(cursor.getColumnIndex(PostDBHandler.COLUMN_ID)));
                post.setComment(cursor.getString(cursor.getColumnIndex(PostDBHandler.COLUMN_COMMENT)));
                post.setImage(cursor.getBlob(cursor.getColumnIndex(PostDBHandler.COLUMN_IMAGE)));
                post.setLocation(cursor.getString(cursor.getColumnIndex(PostDBHandler.COLUMN_LOCATION)));
                post.setPostDate(cursor.getString(cursor.getColumnIndex(PostDBHandler.COLUMN_POST_DATE)));
                post.setUpdatedDate(cursor.getString(cursor.getColumnIndex(PostDBHandler.COLUMN_UPDATE_DATE)));

                posts.add(post);

            }
        }

        close();
        return posts;
    }

    public int updatePost(Post post) {

        open();
        ContentValues values = new ContentValues();
        values.put(PostDBHandler.COLUMN_COMMENT, post.getComment());
        values.put(PostDBHandler.COLUMN_IMAGE, post.getImage());
        values.put(PostDBHandler.COLUMN_LOCATION, post.getLocation());
        values.put(PostDBHandler.COLUMN_POST_DATE, post.getPostDate());
        values.put(PostDBHandler.COLUMN_UPDATE_DATE, post.getUpdatedDate());

        int updatedNum = database.update(PostDBHandler.TABLE_NAME, values,
                PostDBHandler.COLUMN_ID + "=?", new String[] { String.valueOf(post.getPostId()) });
        close();
        return updatedNum;
    }

    public void deletePost(long id) {

        open();
        database.delete(PostDBHandler.TABLE_NAME,
                PostDBHandler.COLUMN_ID + "=?", new String[] { String.valueOf(id) });
        close();
    }

    private void open() {
        Log.i(LOG_TAG, "Database Opened");
        database = dbHandler.getWritableDatabase();
    }

    private void close() {
        Log.i(LOG_TAG, "Database Closed");
        dbHandler.close();
    }
}
