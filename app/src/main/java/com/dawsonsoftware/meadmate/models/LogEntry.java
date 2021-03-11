package com.dawsonsoftware.meadmate.models;

public class LogEntry {
    private Integer id;
    private Integer meadId;
    private String date;
    private String type;
    private String entry;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMeadId() {
        return meadId;
    }

    public void setMeadId(Integer meadId) {
        this.meadId = meadId;
    }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getType() {
        return type;
    }

    public void setType(String type) { this.type = type; }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) { this.entry = entry; }
}
