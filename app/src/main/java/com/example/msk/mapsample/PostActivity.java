package com.example.msk.mapsample;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.msk.mapsample.MapsActivity.REQUEST_CODE_CAMERA;
import static com.example.msk.mapsample.MapsActivity.REQUEST_CODE_GALLERY;


public class PostActivity extends AppCompatActivity {

    private EditText editComment;
    private TextView txtDate, txtCurrentLocation;
    private Button postButton;
    private ImageView postImageView;
    private double latitude, longitude;
    private String address;
    private String imagePath;
    private String currentPhotoPath;

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
                //alert dialog to display options of select image and take photo
                final CharSequence[] items = {"Select image", "Take photo"};
                AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                builder.setTitle("Choose an action");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // Select image
                            //read external storage permission to select image from gallery
                            //runtime permission for devices android 6.0 and above
                            ActivityCompat.requestPermissions(
                                    PostActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_CODE_GALLERY
                            );
                        } else {
                            // Take photo
                            if ( (ActivityCompat.checkSelfPermission(PostActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                                    && (ActivityCompat.checkSelfPermission(PostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ) {
                                invokeCamera();
                            }
                            else
                            {
                                // request permission
                                String[] permissionRequest = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                ActivityCompat.requestPermissions(
                                        PostActivity.this,
                                        permissionRequest,
                                        REQUEST_CODE_CAMERA);
                            }
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // add post record to sqlite database
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // modify txtCurrentLocation TextView to insert post
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
                    Toast.makeText(getApplicationContext(), "Unable to add new post", Toast.LENGTH_LONG).show();
                    // modify txtCurrentLocation TextView to display
                    txtCurrentLocation.setText("Location: " + address);
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

    private void invokeCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = createImageFile();

            // Continue only if the File was successfully created
            if (photoFile != null) {

                // get a file reference
                Uri photoURI = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider",     /* "com.example.msk.mapsample.fileprovider" */
                        photoFile);

                imagePath = photoURI.toString();
                Log.d("imagePath", imagePath);

                // tell the camera where to save the photo.
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                // tell the camera to request WRITE permission.
                takePictureIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
            }
        }
    }

    private File createImageFile() {
        // the public picture director
        File picturesDirectory = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // timestamp makes unique name
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());

        // put together the directory and the timestamp to make a unique image location
        File imageFile = new File(picturesDirectory, "picture" + timestamp + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    private void galleryAddPicture() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPicture() {
        // Get the dimensions of the View
        int targetW = postImageView.getWidth();
        int targetH = postImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        postImageView.setImageBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_GALLERY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // invoke the image gallery using an implicit intent
//                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                    // set the type. Get all image types.
                    galleryIntent.setType("image/*");

                    // invoke this activity, and get something back from it.
                    startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
                }
                else {
                    Toast.makeText(this, "You don't have permission to access file location!",Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case REQUEST_CODE_CAMERA: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    invokeCamera();
                }
                else {
                    Toast.makeText(this, "Cannot use the camera without permission", Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE_GALLERY: {
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
                break;
            }
            case REQUEST_CODE_CAMERA: {
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT);
                    galleryAddPicture();
                    setPicture();
                }
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
                break;
            }
        }
    }
}
