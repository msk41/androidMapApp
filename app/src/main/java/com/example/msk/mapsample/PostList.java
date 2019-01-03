package com.example.msk.mapsample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.msk.mapsample.DB.PostOperations;
import com.example.msk.mapsample.Model.Post;

import java.util.ArrayList;
import java.util.List;

public class PostList extends AppCompatActivity {

    private GridView gridView;
    private List<Post> list;
    private PostListAdapter adapter = null;
    private PostOperations postOps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        gridView = findViewById(R.id.gridView);
        list = new ArrayList<>();
        adapter = new PostListAdapter(this, R.layout.post_items, list);
        gridView.setAdapter(adapter);

        postOps = new PostOperations(this);
        list = postOps.getAllPost();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PostPage.class);
                intent.putExtra("id", list.get(position).getPostId());
                intent.putExtra("Image", list.get(position).getImage());
                intent.putExtra("Location", list.get(position).getLocation());
                intent.putExtra("Comment", list.get(position).getComment());
                startActivity(intent);
            }
        });
    }

}
