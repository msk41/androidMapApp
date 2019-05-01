package com.example.msk.mapsample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.msk.mapsample.Model.Post;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostListAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private int layout;
    private PostFilter postFilter;
    private ArrayList<Post> postList;
    private ArrayList<Post> filteredPostList;

    public PostListAdapter(Context context, int layout, ArrayList<Post> postList) {
        this.context = context;
        this.layout = layout;
        this.postList = postList;
        this.filteredPostList = postList;

        getFilter();
    }

    @Override
    public int getCount() {
        return filteredPostList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredPostList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return filteredPostList.get(position).getPostId();
    }

    @Override
    public Filter getFilter() {
        if (postFilter == null) {
            postFilter = new PostFilter();
        }

        return postFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.postImage = row.findViewById(R.id.imgIcon);
            holder.txtLocation = row.findViewById(R.id.txtLocation);
            holder.txtPostDate = row.findViewById(R.id.txtPostDate);
            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) row.getTag();
        }

        Post post = filteredPostList.get(position);

        holder.txtLocation.setText(post.getLocation());
        holder.txtPostDate.setText(post.getPostDate());

        Uri imageUri = Uri.parse(post.getImage());

        // declare a stream to read the image data from the SD Card.
        InputStream inputStream;

        // get an input stream, based on the URI of the image.
        try {
            inputStream = context.getContentResolver().openInputStream(imageUri);

            // get a bitmap from the stream.
            Bitmap image = BitmapFactory.decodeStream(inputStream);

            // show the image to the user
            holder.postImage.setImageBitmap(image);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return row;
    }

    private class ViewHolder {
        ImageView postImage;
        TextView txtLocation, txtPostDate;
    }

    /*
     * Custom filter for post list
     * Filter content in post list according to the search text
     */
    private class PostFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {

                // search the content in post list
                List<Post> tempList = MapsActivity.mSQLiteHelper.search(constraint.toString());

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = postList.size();
                filterResults.values = postList;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredPostList = (ArrayList<Post>) results.values;
            Collections.reverse(filteredPostList);
            notifyDataSetChanged();
        }
    }

}
