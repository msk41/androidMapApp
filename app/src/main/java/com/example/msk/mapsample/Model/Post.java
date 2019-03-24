package com.example.msk.mapsample.Model;

public class Post {

    private int    postId;
    private String comment;
    private String image;
    private String location;
    private double latitude;
    private double longitude;
    private String postDate;
    private String updatedDate;

    public Post(int postId, String comment, String image, String location, double latitude, double longitude, String postDate, String updatedDate) {
        this.postId = postId;
        this.comment = comment;
        this.image = image;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.postDate = postDate;
        this.updatedDate = updatedDate;
    }

    public Post() {
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {

        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {

        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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
