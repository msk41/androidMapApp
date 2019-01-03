package com.example.msk.mapsample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.msk.mapsample.Model.Post;

import java.util.ArrayList;
import java.util.List;

public class PostListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<Post> postsList;

    public PostListAdapter(Context context, int layout, List<Post> postsList) {
        this.context = context;
        this.layout = layout;
        this.postsList = postsList;
    }

    @Override
    public int getCount() {
        return postsList.size();
    }

    @Override
    public Object getItem(int position) {
        return postsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView comment, location;
        ImageView postImage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.postImage = row.findViewById(R.id.postItemImageView);
            holder.comment = row.findViewById(R.id.postItemTextView);
            holder.location = row.findViewById(R.id.locaitonTextView);
            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) row.getTag();
        }

        Post post = postsList.get(position);

        holder.comment.setText(post.getComment());
        holder.location.setText(post.getLocation());

        byte[] postImage = post.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(postImage, 0, postImage.length);
        holder.postImage.setImageBitmap(bitmap);

        return row;
    }

}
