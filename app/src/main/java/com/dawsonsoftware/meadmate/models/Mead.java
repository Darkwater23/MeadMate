package com.dawsonsoftware.meadmate.models;

import java.util.Date;

public class Mead {

    private int id;
    private String name;
    private String startDate;
    private String description;
    private String originalGravity;
    private boolean archived;

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() { return startDate; }

    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOriginalGravity() {
        return originalGravity;
    }

    public void setOriginalGravity(String originalGravity) { this.originalGravity = originalGravity; }

    public boolean getArchived() { return archived; }

    public void setArchived(boolean archived) { this.archived = archived; }
}
