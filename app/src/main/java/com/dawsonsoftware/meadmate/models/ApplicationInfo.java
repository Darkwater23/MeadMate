package com.dawsonsoftware.meadmate.models;

import com.dawsonsoftware.meadmate.MeadMateData;

import java.time.LocalDate;

public class ApplicationInfo {

    private int versionNumber;
    private String versionName;
    private int databaseVersion;
    private String dateUpdated;

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getDatabaseVersion() {
        return databaseVersion;
    }

    public void setDatabaseVersion(int databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    public String getDateUpdated() { return dateUpdated; }

    public void setDateUpdated(String dateUpdated) { this.dateUpdated = dateUpdated; }
}
