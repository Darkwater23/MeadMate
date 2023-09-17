package com.dawsonsoftware.meadmate.models;

public class CombinedMeadRecord {
    private int meadId;
    private String batchName;
    private String startDate;
    private String description;
    private String startingGravity;
    private String archived;
    private String tags;
    private String eventDate;
    private String eventName;
    private String eventType;
    private String eventDescription;

    public int getMeadId() {
        return meadId;
    }

    public void setMeadId(int meadId) {
        this.meadId = meadId;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartingGravity() {
        return startingGravity;
    }

    public void setStartingGravity(String startingGravity) {
        this.startingGravity = startingGravity;
    }

    public String getArchived() {
        return archived;
    }

    public void setArchived(String archived) {
        this.archived = archived;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}