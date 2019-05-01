package com.example.msk.mapsample;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import static com.example.msk.mapsample.MapsActivity.REQUEST_CODE_GALLERY;

public class PostPage extends AppCompatActivity {

    private ImageView postImageView;
    private TextView txtLocation, txtComment;
    private Button editButton;
    private String postComment;
    private int postId;
    private Bitmap bitmap = null;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_page);
        init();

        // set post page
        final Intent intent = getIntent();
        postId = intent.getIntExtra("id", 0);

        Cursor cursor = MapsActivity.mSQLiteHelper.getPost("SELECT comment, " +
                                                                      " image, " +
                                                                      " location " +
                                                                      " FROM POST " +
                                                                      " WHERE id =" + postId);

        cursor.moveToFirst();
        String comment = cursor.getString(0);
        String image = cursor.getString(1);
        String location = cursor.getString(2);

        Uri imageUri = Uri.parse(image);
        imagePath = imageUri.toString();
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        postImageView.setImageBitmap(bitmap);
        txtComment.setText(comment);
        postComment = comment;
        txtLocation.setText(location);

        // update or delete post record by on editButton click
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //alert dialog to display options of update and delete
                final CharSequence[] items = {"Update", "Delete"};
                AlertDialog.Builder builder =  new AlertDialog.Builder(PostPage.this);
                builder.setTitle("Choose an action");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // update
                            showDialogUpdate(PostPage.this, postId);
                        }
                        else
                        {
                            // delete
                            showDialogDelete(postId);
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void init() {
        postImageView = findViewById(R.id.updateImageView);
        txtLocation = findViewById(R.id.locationTextView);
        txtComment = findViewById(R.id.commentTextView);
        editButton = findViewById(R.id.editButton);
    }

    private void showDialogDelete(final int postId) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(PostPage.this);
        dialogDelete.setTitle("Warning!");
        dialogDelete.setMessage("Are you sure you want to delete this?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    MapsActivity.mSQLiteHelper.deletePost(postId);
                    Toast.makeText(getApplicationContext(), "Delete successfully!", Toast.LENGTH_SHORT).show();
                    Intent deleteIntent = new Intent(getApplicationContext(), PostList.class);
                    startActivity(deleteIntent);
                }
                catch (Exception e)
                {
                    Log.e("Delete error", e.getMessage());
                }
            }
        });
        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void showDialogUpdate(Activity activity, final int postId) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_dialog);
        dialog.setTitle("Update");

        // set update dialog
        ImageView updateImageView = dialog.findViewById(R.id.updateImageView);
        updateImageView.setImageBitmap(bitmap);
        final EditText editComment = dialog.findViewById(R.id.commentEditText);
        editComment.setText(postComment);
        Button updateButton = dialog.findViewById(R.id.updateButton);

        // set width of dialog
        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        // set height of dialog
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        // click imageView to update image
        updateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check external storage permission
                ActivityCompat.requestPermissions(
                        PostPage.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Date currentDateTime = Calendar.getInstance().getTime();
                    MapsActivity.mSQLiteHelper.updatePost(
                            editComment.getText().toString().trim(),
                            imagePath,
                            PostActivity.DateToString(currentDateTime),
                            postId
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Update successfully!", Toast.LENGTH_SHORT).show();
                    Intent updateIntent = new Intent(getApplicationContext(), PostList.class);
                    startActivity(updateIntent);
                }
                catch (Exception e)
                {
                    Log.e("Update error", e.getMessage());
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

                // get an input stream, based on the URI of the image.
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);

                    // get a bitmap from the stream.
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    // show the image to the user
                    postImageView.setImageBitmap(bitmap);
                    imagePath = imageUri.toString();
                    Log.i("imagePath", imagePath);

                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
