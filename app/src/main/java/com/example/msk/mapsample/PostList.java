package com.example.msk.mapsample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.msk.mapsample.Model.Post;

import java.util.ArrayList;

import static com.example.msk.mapsample.MapsActivity.REQUEST_CODE_READ_EXTERNAL_STORAGE;

public class PostList extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Post> list;
    private PostListAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Post List");

        listView = findViewById(R.id.listView);
        list = new ArrayList<>();


        // check if API level is larger than 23
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_READ_EXTERNAL_STORAGE);
            }
            else {
                adapter = new PostListAdapter(this, R.layout.post_items, list);
                listView.setAdapter(adapter);
            }
        }

        // show Post list
        Cursor cursor = MapsActivity.mSQLiteHelper.getPost("SELECT * FROM POST");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String comment = cursor.getString(1);
            String image = cursor.getString(2);
            String location = cursor.getString(3);
            double latitude = cursor.getDouble(4);
            double longitude = cursor.getDouble(5);
            String postDate = cursor.getString(6);
            String updatedDate = cursor.getString(7);
            list.add(new Post(id, comment, image, location, latitude, longitude, postDate, updatedDate));
        }
        adapter.notifyDataSetChanged();

        if (list.size()==0){
            //if there is no record in table of database which means listview is empty
            Toast.makeText(this, "No record found...", Toast.LENGTH_SHORT).show();
        }

        // move to the one Post page
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PostPage.class);
                intent.putExtra("id", list.get(position).getPostId());
                intent.putExtra("Comment", list.get(position).getComment());
                intent.putExtra("Image", list.get(position).getImage());
                intent.putExtra("Location", list.get(position).getLocation());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do stuff
                }
                break;
            }
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            }
        }
    }
}
