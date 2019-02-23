package com.example.msk.mapsample;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.msk.mapsample.Model.Post;

import java.util.ArrayList;

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
        adapter = new PostListAdapter(this, R.layout.post_items, list);
        listView.setAdapter(adapter);

        Cursor cursor = MapsActivity.mSQLiteHelper.getPost("SELECT * FROM POST");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String comment = cursor.getString(1);
            byte[] image = cursor.getBlob(2);
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

}
