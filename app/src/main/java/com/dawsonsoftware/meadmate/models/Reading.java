package com.dawsonsoftware.meadmate.models;

public class Reading {

    private Integer id;
    private Integer brewId;
    private String date;
    private String specificGravity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBrewId() {
        return brewId;
    }

    public void setBrewId(Integer brewId) {
        this.brewId = brewId;
    }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getSpecificGravity() {
        return specificGravity;
    }

    public void setSpecificGravity(String specificGravity) { this.specificGravity = specificGravity; }
}
