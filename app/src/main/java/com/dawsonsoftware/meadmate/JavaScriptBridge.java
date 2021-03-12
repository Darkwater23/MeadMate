package com.dawsonsoftware.meadmate;

import android.app.Activity;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.dawsonsoftware.meadmate.models.LogEntry;
import com.dawsonsoftware.meadmate.models.Mead;
import com.dawsonsoftware.meadmate.models.Reading;
import com.google.gson.Gson;

import java.text.ParseException;
import java.util.List;

import static java.lang.Integer.parseInt;

public class JavaScriptBridge {
    private Activity activity;
    private MeadMateData data;

    public JavaScriptBridge(Activity activity) {
        this.activity = activity;
        data = new MeadMateData(activity);
    }

    @JavascriptInterface
    public void exportData()
    {
        Log.i("JavaScriptBridge", "Exporting app data.");

        //TODO: Add method for exporting data

        Log.i("JavaScriptBridge", "Export complete!");
    }

    @JavascriptInterface
    public String importData(String filePath)
    {
        Log.i("JavaScriptBridge", "Importing app data.");
        Log.d("JavaScriptBridge", "Path to user-selected file:" + filePath);

        String result = "Not implemented yet.";

        //TODO: Add method for exporting data

        Log.i("JavaScriptBridge", "Import complete!");

        return result;
    }

    @JavascriptInterface
    public String fetchMeads(){

        Log.i("JavaScriptBridge", "Fetching mead data.");

        List<Mead> meads = data.getMeads();

        Log.i("JavaScriptBridge", "Fetched " + meads.size() + " records.");

        Gson gson = new Gson();

        String json = gson.toJson(meads);

        Log.d("JavaScriptBridge", json);

        return json;
    }

    @JavascriptInterface
    public String fetchReadings(String meadId){

        Log.i("JavaScriptBridge", "Fetching readings data for mead " + meadId + ".");

        List<Reading> readings = data.getReadings(parseInt(meadId));

        Log.i("JavaScriptBridge", "Fetched " + readings.size() + " records.");

        Gson gson = new Gson();

        String json = gson.toJson(readings);

        Log.d("JavaScriptBridge", json);

        return json;
    }

    @JavascriptInterface
    public String fetchLogEntries(String meadId){

        Log.i("JavaScriptBridge", "Fetching log entry data for mead " + meadId + ".");

        List<LogEntry> logEntries = data.getLogEntries(parseInt(meadId));

        Log.i("JavaScriptBridge", "Fetched " + logEntries.size() + " records.");

        Gson gson = new Gson();

        String json = gson.toJson(logEntries);

        Log.d("JavaScriptBridge", json);

        return json;
    }

    @JavascriptInterface
    public String fetchLogEntry(String id)
    {
        Log.i("JavaScriptBridge", "Fetching log entry data for log entry id " + id + ".");

        String entry = data.getLogEntry(parseInt(id));

        return entry;
    }

    @JavascriptInterface
    public String fetchMead(String id)
    {
        Log.i("JavaScriptBridge", "Fetching mead data by ID: " + id);

        Mead mead = data.getMead(parseInt(id));

        Gson gson = new Gson();

        String json = gson.toJson(mead);

        Log.d("JavaScriptBridge", json);

        return json;
    }

    @JavascriptInterface
    public void addMead(String name, String startDate, String originalGravity, String description) throws ParseException {

        Log.d("JavaScriptBridge", "Adding mead record for: " + name);

        Mead mead = new Mead();

        mead.setName(name);
        mead.setStartDate(startDate);
        mead.setDescription(description);
        mead.setOriginalGravity(originalGravity);

        data.addMead(mead);
    }

    @JavascriptInterface
    public void addReading(String meadId, String date, String specificGravity) throws ParseException {

        Log.d("JavaScriptBridge", "Adding reading record for: " + meadId);

        Reading reading = new Reading();

        reading.setMeadId(parseInt(meadId));
        reading.setDate(date);
        reading.setSpecificGravity(specificGravity);

        data.addReading(reading);
    }

    @JavascriptInterface
    public void addLogEntry(String meadId, String date, String type, String entry) throws ParseException {

        Log.d("JavaScriptBridge", "Adding log entry record for: " + meadId);

        LogEntry logEntry = new LogEntry();

        logEntry.setMeadId(parseInt(meadId));
        logEntry.setDate(date);
        logEntry.setTypeId(parseInt(type));
        logEntry.setEntry(entry);

        data.addLogEntry(logEntry);
    }

    @JavascriptInterface
    public void deleteMead(String id)
    {
        Log.i("JavaScriptBridge", "Deleting mead by id: " + id);

        data.deleteMead(parseInt(id));
    }

    @JavascriptInterface
    public void deleteReading(String id)
    {
        Log.i("JavaScriptBridge", "Deleting reading by id: " + id);

        data.deleteReading(parseInt(id));
    }

    @JavascriptInterface
    public void deleteLogEntry(String id)
    {
        Log.i("JavaScriptBridge", "Deleting log entry by id: " + id);

        data.deleteLogEntry(parseInt(id));
    }

    @JavascriptInterface
    public void logError(String tag, String message)
    {
        Log.e(tag, message);
    }

    @JavascriptInterface
    public void logDebug(String tag, String message)
    {
        Log.d(tag, message);
    }

    @JavascriptInterface
    public void logInfo(String tag, String message)
    {
        Log.i(tag, message);
    }
}