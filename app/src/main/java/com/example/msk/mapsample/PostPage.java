package com.example.msk.mapsample;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

public class PostPage extends AppCompatActivity {

    private ImageView postImageView;
    private TextView txtLocation, txtComment;
    private Button editButton;
    private int postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_page);
        init();

        final Intent intent = getIntent();
        postId = intent.getIntExtra("id", 0);
        Bitmap bitmap = BitmapFactory.decodeByteArray(intent.getByteArrayExtra("Image"),
                0,
                intent.getByteArrayExtra("Image").length);
        postImageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
                                            postImageView.getWidth(),
                                            postImageView.getHeight(),
                                            false));
        txtLocation.setText(intent.getStringExtra("Location"));
        txtComment.setText(intent.getStringExtra("Comment"));

        // update or delete post record by on editButton click
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //alert dialog to display options of update and delete
                final CharSequence[] items = {"Update", "Delete"};
                AlertDialog.Builder dialog =  new AlertDialog.Builder(getApplicationContext());
                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
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
                dialog.show();
            }
        });
    }

    private void init() {
        postImageView = findViewById(R.id.postImageView);
        txtLocation = findViewById(R.id.locationTextView);
        txtComment = findViewById(R.id.commentTextView);
        editButton = findViewById(R.id.editButton);
    }

    private void showDialogDelete(final int postId) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(getApplicationContext());
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
                    Log.e("error", e.getMessage());
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

        postImageView = dialog.findViewById(R.id.postImageView);
        final EditText editComment = dialog.findViewById(R.id.commentEditText);
        Button updateButton = dialog.findViewById(R.id.updateButton);

        // set width of dialog
        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        // set height of dialog
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        // click imageView to update image
        postImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check external storage permission
                ActivityCompat.requestPermissions(
                        PostPage.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
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
                            PostActivity.imageViewToByte(postImageView),
                            PostActivity.DateToString(currentDateTime),
                            postId
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Update Successfull", Toast.LENGTH_SHORT).show();
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
        if (requestCode == 888) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 888);
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

        if (requestCode == 888 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
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
