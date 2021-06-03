package com.dawsonsoftware.meadmate.models;

import com.dawsonsoftware.meadmate.MeadMateData;

public class ApplicationInfo {

    private int versionNumber;
    private String versionName;
    private int databaseVersion;

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
}
