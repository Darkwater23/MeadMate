package com.dawsonsoftware.meadmate.models;

public class LogEntry {
    private Integer id;
    private Integer meadId;
    private String date;
    private Integer typeId;
    private String typeName;
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

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) { this.typeId = typeId; }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) { this.entry = entry; }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
