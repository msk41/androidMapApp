package com.example.msk.mapsample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msk.mapsample.DB.PostOperations;
import com.example.msk.mapsample.Model.Post;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PostActivity extends AppCompatActivity {

    private static final String EXTRA_ADD_UPDATE = "com.example.msk.mapsample.add_update";

    private EditText commentEditText;
    private TextView dateTextView, currentLocationTextView;
    private Button postButton;
    private ImageView postImageView;
    private String mode;
    private long postId;
    private PostOperations postData;
    private Post oldPost;
    private Post newPost;

    final int REQUEST_CODE_GALLERY = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        init();
        final Date currentDateTime = Calendar.getInstance().getTime();
        dateTextView.setText("Date: " + DateToString(currentDateTime));

        // display current location
        Intent intent = getIntent();
        if (intent != null) {
            String address = intent.getStringExtra("address");
            currentLocationTextView.setText("Location: " + address);
        }

        newPost = new Post();
        oldPost = new Post();
        postData = new PostOperations(this);

        mode = getIntent().getStringExtra(EXTRA_ADD_UPDATE);
        if (mode.equals("Update")) {
            postButton.setText("Update");
            postId = getIntent().getLongExtra("id", 0);
            initializePost(postId);
        }

        postImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        PostActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals("Add")) {
                    try {
                        newPost.setComment(commentEditText.getText().toString());
                        newPost.setImage(imageViewToByte(postImageView));
                        newPost.setLocation(currentLocationTextView.getText().toString());
                        newPost.setPostDate(DateToString(currentDateTime));
                        newPost.setUpdatedDate(DateToString(currentDateTime));

                        postData.addPost(newPost);
                        Toast.makeText(getApplicationContext(), "Added successfully!", Toast.LENGTH_SHORT).show();
                        commentEditText.setText("");
                        dateTextView.setText("");
                        currentLocationTextView.setText("");
                        postImageView.setImageResource(R.mipmap.ic_launcher);
                        Intent addIntent = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(addIntent);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (mode.equals("Update"))
                {
                    try {
                        oldPost.setComment(commentEditText.getText().toString());
                        oldPost.setImage(imageViewToByte(postImageView));
                        oldPost.setUpdatedDate(DateToString(currentDateTime));
                        postData.updatePost(oldPost);
                        Toast.makeText(getApplicationContext(), "Updated successfully!", Toast.LENGTH_SHORT).show();
                        Intent updateIntent = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(updateIntent);
                    }
                    catch (Exception e ) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void init() {
        commentEditText = findViewById(R.id.commentEditText);
        dateTextView = findViewById(R.id.dateTextView);
        currentLocationTextView = findViewById(R.id.currentLocationTextView);
        postButton = findViewById(R.id.postButton);
        postImageView = findViewById(R.id.postItemImageView);
    }

    private void initializePost(long postId) {
        oldPost = postData.getPost(postId);
        commentEditText.setText(oldPost.getComment());
        currentLocationTextView.setText(oldPost.getLocation());

        byte[] postImage = oldPost.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(postImage, 0, postImage.length);
        postImageView.setImageBitmap(bitmap);
    }

    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ( (BitmapDrawable)image.getDrawable() ).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private String DateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String formatDate = sdf.format(date);
        return formatDate;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!",Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                postImageView.setImageBitmap(bitmap);

            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
