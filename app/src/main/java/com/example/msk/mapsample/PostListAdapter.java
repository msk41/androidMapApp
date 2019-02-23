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
    private ArrayList<Post> postList;

    public PostListAdapter(Context context, int layout, ArrayList<Post> postList) {
        this.context = context;
        this.layout = layout;
        this.postList = postList;
    }

    @Override
    public int getCount() {
        return postList.size();
    }

    @Override
    public Object getItem(int position) {
        return postList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView postImage;
        TextView txtComment, txtLocation, txtPostDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.postImage = row.findViewById(R.id.imgIcon);
            holder.txtComment = row.findViewById(R.id.txtComment);
            holder.txtLocation = row.findViewById(R.id.txtLocation);
            holder.txtPostDate = row.findViewById(R.id.txtPostDate);
            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) row.getTag();
        }

        Post post = postList.get(position);

        holder.txtComment.setText(post.getComment());
        holder.txtLocation.setText(post.getLocation());
        holder.txtPostDate.setText(post.getPostDate());

        byte[] postImage = post.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(postImage, 0, postImage.length);
        holder.postImage.setImageBitmap(bitmap);

        return row;
    }

}
