package com.example.msk.mapsample;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.msk.mapsample.Model.Post;

import java.util.ArrayList;

import static com.example.msk.mapsample.MapsActivity.REQUEST_CODE_READ_EXTERNAL_STORAGE;

public class PostList extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private ListView postListView;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private ArrayList<Post> postList;
    private PostListAdapter postListAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Post List");

        postListView = findViewById(R.id.postListView);
        postList = new ArrayList<>();


        // check if API level is larger than 23
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_READ_EXTERNAL_STORAGE);
            }
            else {
                postListAdapter = new PostListAdapter(this, R.layout.post_items, postList);
                postListView.setAdapter(postListAdapter);
            }
        }

        // show Post list
        Cursor cursor = MapsActivity.mSQLiteHelper.getPost("SELECT id, " +
                                                                      " image, " +
                                                                      " location, " +
                                                                      " postDate " +
                                                                      " FROM POST " +
                                                                      " ORDER BY postDate DESC");
        postList.clear();
        while (cursor.moveToNext()) {
            int postId = cursor.getInt(0);
            String image = cursor.getString(1);
            String location = cursor.getString(2);
            String postDate = cursor.getString(3);
            Post post = new Post();
            post.setPostId(postId);
            post.setImage(image);
            post.setLocation(location);
            post.setPostDate(postDate);
            postList.add(post);
        }
        postListAdapter.notifyDataSetChanged();

        if (postList.size()==0){
            //if there is no record in table of database which means listview is empty
            Toast.makeText(this, "No record found...", Toast.LENGTH_SHORT).show();
        }

        // move to the one Post page
        postListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // close search view if it's visible
//                if (searchView.isShown()) {
//                    searchMenuItem.collapseActionView();
//                    searchView.setQuery("", false);
//                }
                Intent intent = new Intent(getApplicationContext(), PostPage.class);
                intent.putExtra("id", (int) postListAdapter.getItemId(position));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        // Do not iconify the widget; expand it by default
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        postListAdapter.getFilter().filter(newText);

        return true;
    }
}
