package com.example.msk.mapsample;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msk.mapsample.DB.PostOperations;
import com.example.msk.mapsample.Model.Post;

public class PostPage extends AppCompatActivity {

    private static final String EXTRA_ADD_UPDATE = "com.example.msk.mapsample.add_update";

    private ImageView postImageView;
    private TextView locationTextView, commentTextView;
    private Button editButton;
    private PostOperations postData;
    private long postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_page);
        init();

        final Intent intent = getIntent();
        postId = intent.getLongExtra("id", 0);
        Bitmap bitmap = BitmapFactory.decodeByteArray(intent.getByteArrayExtra("Image"),
                0,
                intent.getByteArrayExtra("Image").length);
        postImageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
                                            postImageView.getWidth(),
                                            postImageView.getHeight(),
                                            false));
        locationTextView.setText(intent.getStringExtra("Location"));
        commentTextView.setText(intent.getStringExtra("Comment"));

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence[] items = {"Update", "Delete"};
                AlertDialog.Builder dialog =  new AlertDialog.Builder(getApplicationContext());
                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // update
                            Intent updateIntent = new Intent(getApplicationContext(), PostActivity.class);
                            updateIntent.putExtra(EXTRA_ADD_UPDATE, "Update");
                            updateIntent.putExtra("id", postId);
                            startActivity(updateIntent);
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
        locationTextView = findViewById(R.id.locaitonTextView);
        commentTextView = findViewById(R.id.commentTextView);
        editButton = findViewById(R.id.editButton);
    }

    private void showDialogDelete(final long postId) {
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(getApplicationContext());

        dialogDelete.setTitle("Warning!");
        dialogDelete.setMessage("Are you sure you want to delete this?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postData = new PostOperations(getApplicationContext());
                postData.deletePost(postId);
                Toast.makeText(getApplicationContext(), "Delete successfully!", Toast.LENGTH_SHORT).show();
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
}
