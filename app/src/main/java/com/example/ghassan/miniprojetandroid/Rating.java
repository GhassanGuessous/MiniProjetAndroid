package com.example.ghassan.miniprojetandroid;

/**
 * Created by Ghassan on 26/04/2018.
 */

public class Rating {

    private String userId;
    private int rating;

    public Rating() {
    }

    public Rating(String userId, int rating) {
        this.userId = userId;
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
