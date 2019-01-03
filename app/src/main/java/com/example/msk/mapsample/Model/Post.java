package com.example.msk.mapsample.Model;

public class Post {

    private long   postId;
    private String comment;
    private byte[] image;
    private String location;
    private String postDate;
    private String updatedDate;

    public Post(long postId, String comment, byte[] image, String location, String postDate, String updatedDate) {
        this.postId = postId;
        this.comment = comment;
        this.image = image;
        this.location = location;
        this.postDate = postDate;
        this.updatedDate = updatedDate;
    }

    public Post() {
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getLocation() {

        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

}
