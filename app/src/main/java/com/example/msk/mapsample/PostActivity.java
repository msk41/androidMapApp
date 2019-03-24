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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.msk.mapsample.MapsActivity.REQUEST_CODE_GALLERY;


public class PostActivity extends AppCompatActivity {

    private EditText editComment;
    private TextView txtDate, txtCurrentLocation;
    private Button postButton;
    private ImageView postImageView;
    private double latitude, longitude;
    private String address;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("New Post");
        init();
        final Date currentDateTime = Calendar.getInstance().getTime();
        txtDate.setText("Date: " + DateToString(currentDateTime));

        // display current location
        Intent intent = getIntent();
        if (intent != null) {
            address = intent.getStringExtra("address");
            txtCurrentLocation.setText("Location: " + address);
            latitude = intent.getDoubleExtra("latitude", 0);
            longitude = intent.getDoubleExtra("longitude", 0);
        }

        // select image by on imageView click
        postImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //read external storage permission to select image from gallery
                //runtime permission for devices android 6.0 and above
                ActivityCompat.requestPermissions(
                        PostActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });

        // add post record to sqlite database
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    txtCurrentLocation.setText(address);
                    MapsActivity.mSQLiteHelper.insertPost(
                            editComment.getText().toString().trim(),
                            imagePath,
                            txtCurrentLocation.getText().toString().trim(),
                            latitude,
                            longitude,
                            DateToString(currentDateTime),
                            DateToString(currentDateTime)
                    );
                    Toast.makeText(getApplicationContext(), "Added successfully!", Toast.LENGTH_SHORT).show();
                    // reset views
                    editComment.setText("");
                    txtDate.setText("");
                    txtCurrentLocation.setText("");
                    postImageView.setImageResource(R.mipmap.ic_launcher);
                    Intent addIntent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(addIntent);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() {
        editComment = findViewById(R.id.commentEditText);
        txtDate = findViewById(R.id.dateTextView);
        txtCurrentLocation = findViewById(R.id.currentLocationTextView);
        postButton = findViewById(R.id.postButton);
        postImageView = findViewById(R.id.updateImageView);
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ( (BitmapDrawable)image.getDrawable() ).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static String DateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String formatDate = sdf.format(date);
        return formatDate;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // invoke the image gallery using an implict intent
//                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                // set the type. Get all image types.
                galleryIntent.setType("image/*");

                // invoke this activity, and get something back from it.
                startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
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

        if (requestCode == REQUEST_CODE_GALLERY) {
            if (resultCode == RESULT_OK && data != null) {

                // the address of the image on the SD Card.
                Uri imageUri = data.getData();

                // declare a stream to read the image data from the SD Card.
                InputStream inputStream;

                // get an input stream, based on the URI of the image.
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);

                    // get a bitmap from the stream.
                    Bitmap image = BitmapFactory.decodeStream(inputStream);

                    // show the image to the user
                    postImageView.setImageBitmap(image);
                    imagePath = imageUri.toString();
                    Log.i("imagePath", imagePath);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
