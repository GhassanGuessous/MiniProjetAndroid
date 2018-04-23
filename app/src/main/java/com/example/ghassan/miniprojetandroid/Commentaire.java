package com.example.ghassan.miniprojetandroid;

/**
 * Created by Ghassan on 20/04/2018.
 */

public class Commentaire {

    private String user;
    private String comment;

    public Commentaire() {
    }

    public Commentaire(String user, String comment) {
        this.user = user;
        this.comment = comment;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Commentaire{" +
                "user='" + user + '\'' +
                ", contenu='" + comment + '\'' +
                '}';
    }
}
