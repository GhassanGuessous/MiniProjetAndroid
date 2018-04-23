package com.example.ghassan.miniprojetandroid;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Ghassan on 11/04/2018.
 */

public class Endroit{

    private String id;
    private String nom;
    private String adresse;
    private String tag;
    private String userId;
    private String imageFilePath;
    private double lat;
    private double lng;

    public Endroit(){

    }

    public Endroit(String id, String nom, String adresse, String tag, String userId, String imageFilePath, double lat, double lng) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.tag = tag;
        this.userId = userId;
        this.imageFilePath = imageFilePath;
        this.lat = lat;
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Endroit{" +
                "nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", tag='" + tag + '\'' +
                ", userId='" + userId + '\'' +
                ", imageFilePath='" + imageFilePath + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
