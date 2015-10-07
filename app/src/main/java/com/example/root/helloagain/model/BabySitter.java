package com.example.root.helloagain.model;

/**
 * Created by Ayaz Pasha on 10/9/15.
 */
public class BabySitter {

    private String name;
    private String category;
    private double latitude;
    private double longitude;
    private String address;
    private String yearOfEst;
    private double distance;

    public BabySitter(String name, String category, double latitude, double longitude, String address, String yearOfEst, double distance) {
        this.name = name;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.yearOfEst = yearOfEst;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getYearOfEst() {
        return yearOfEst;
    }

    public void setYearOfEst(String yearOfEst) {
        this.yearOfEst = yearOfEst;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
