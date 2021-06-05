/*
This file is part of Mead Mate.

Mead Mate is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Mead Mate is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Mead Mate.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.dawsonsoftware.meadmate;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.loader.app.LoaderManager;

import com.dawsonsoftware.meadmate.models.ApplicationInfo;
import com.dawsonsoftware.meadmate.models.Event;
import com.dawsonsoftware.meadmate.models.EventType;
import com.dawsonsoftware.meadmate.models.Mead;
import com.dawsonsoftware.meadmate.models.Reading;
import com.dawsonsoftware.meadmate.models.Tag;
import com.google.gson.Gson;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static java.lang.Integer.parseInt;

public class JavaScriptBridge {
    private final Activity activity;
    private final MeadMateData data;

    public JavaScriptBridge(Activity activity) {
        this.activity = activity;
        data = new MeadMateData(activity);
    }

    /*@JavascriptInterface
    public void exportData()
    {
        Log.i("JavaScriptBridge", "Exporting app data.");

        //TODO: Add method for exporting data

        Log.i("JavaScriptBridge", "Export complete!");
    }*/

    /*@JavascriptInterface
    public String importData(String filePath)
    {
        Log.i("JavaScriptBridge", "Importing app data.");
        Log.d("JavaScriptBridge", "Path to user-selected file:" + filePath);

        String result = "Not implemented yet.";

        //TODO: Add method for exporting data

        Log.i("JavaScriptBridge", "Import complete!");

        return result;
    }*/

    @JavascriptInterface
    public String fetchMeads(String sortBy, boolean includeArchived){

        Log.i("JavaScriptBridge", "Fetching mead data.");

        //private static final String KEY_MEAD_ID = "_ID";
        //private static final String KEY_MEAD_NAME = "NAME";
        //private static final String KEY_MEAD_START_DATE = "START_DATE";

        //<option value="byId">Record Id (Oldest First)</option>
        //<option value="byName">Mead Name (A-Z)</option>
        //<option value="byDate">Mead Start Date (Oldest First)</option>
        //<option value="byIdDesc">Record Id (Newest First)</option>
        //<option value="byNameDesc">Mead Name (Z-A)</option>
        //<option value="byDateDesc">Mead Start Date (Newest First)</option>

        String orderBy;

        switch(sortBy)
        {
            case "byId":
                orderBy = "_ID";
                break;
            case "byName":
                orderBy = "NAME";
                break;
            case "byDate":
                orderBy = "START_DATE, _ID"; // The dates don't have a time portion, so use the record ID to put them in input order.
                break;
            case "byIdDesc":
                orderBy = "_ID DESC";
                break;
            case "byNameDesc":
                orderBy = "NAME DESC";
                break;
            case "byDateDesc":
                orderBy = "START_DATE DESC, _ID DESC"; // The dates don't have a time portion, so use the record ID to put them in input order.
                break;
            default:
                orderBy = null;
        }

        List<Mead> meads = data.getMeads(orderBy, includeArchived);

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
    public String fetchEvents(String meadId){

        Log.i("JavaScriptBridge", "Fetching event data for mead " + meadId + ".");

        List<Event> events = data.getEvents(parseInt(meadId));

        Log.i("JavaScriptBridge", "Fetched " + events.size() + " records.");

        Gson gson = new Gson();

        String json = gson.toJson(events);

        Log.d("JavaScriptBridge", json);

        return json;
    }

    @JavascriptInterface
    public String fetchEventTypes(){

        Log.i("JavaScriptBridge", "Fetching event types.");

        List<EventType> eventTypes = data.getEventTypes();

        Log.i("JavaScriptBridge", "Fetched " + eventTypes.size() + " records.");

        Gson gson = new Gson();

        String json = gson.toJson(eventTypes);

        Log.d("JavaScriptBridge", json);

        return json;
    }

    @JavascriptInterface
    public String fetchEventDescription(String id)
    {
        Log.i("JavaScriptBridge", "Fetching event description data for event id " + id + ".");

        return data.getEventDescription(parseInt(id));
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
    public String fetchTags()
    {
        Log.i("JavaScriptBridge", "Fetching tags.");

        List<Tag> tags = data.getTags();

        Log.i("JavaScriptBridge", "Fetched " + tags.size() + " records.");

        Gson gson = new Gson();

        String json = gson.toJson(tags);

        Log.d("JavaScriptBridge", json);

        return json;
    }

    @JavascriptInterface
    public String fetchMeadTags(String meadId)
    {
        Log.i("JavaScriptBridge", "Fetching tags for mead: " + meadId);

        List<Tag> tags = data.getMeadTags(parseInt(meadId));

        Log.i("JavaScriptBridge", "Fetched " + tags.size() + " records.");

        Gson gson = new Gson();

        String json = gson.toJson(tags);

        Log.d("JavaScriptBridge", json);

        return json;
    }

    @JavascriptInterface
    public void addMead(String name, String startDate, String originalGravity, String description) {

        Log.d("JavaScriptBridge", "Adding mead record for mead: " + name);

        Mead mead = new Mead();

        mead.setName(name);
        mead.setStartDate(startDate);
        mead.setDescription(description);
        mead.setOriginalGravity(originalGravity);

        Integer meadId = data.addMead(mead);

        // Create primary fermentation event
        if(meadId > 0)
        {
            Event event = new Event();

            event.setDate(mead.getStartDate());
            event.setDescription("");
            event.setMeadId(meadId);
            event.setTypeId(1);

            data.addEvent(event);
        }
    }

    @JavascriptInterface
    public void addMeadTag(String meadId, String tag)
    {
        if(meadId == null || meadId.isEmpty())
        {
            Log.e("JavaScriptBridge", "MeadId is null or empty");
            return;
        }

        if(tag == null || tag.isEmpty())
        {
            Log.e("JavaScriptBridge", "Tag value is null or empty");
            return;
        }

        Log.d("JavaScriptBridge", "Adding '" + tag + "' tag to mead " + meadId);

        // Create or fetch tag's ID
        Integer tagId = data.addTag(tag);

        // Associate tag with mead by ID
        if(tagId > 0)
        {
            data.addMeadTag(parseInt(meadId), tagId);
        }
    }

    //window.Android.updateMead(mId, mName, mDate, mGravity, mDesc);
    @JavascriptInterface
    public void updateMead(String id, String name, String startDate, String originalGravity, String description)
    {
        Log.d("JavaScriptBridge", "Updating mead record for mead: " + name);

        Mead mead = new Mead();

        mead.setId(parseInt(id));
        mead.setName(name);
        mead.setStartDate(startDate);
        mead.setDescription(description);
        mead.setOriginalGravity(originalGravity);

        data.updateMead(mead);
    }

    @JavascriptInterface
    public void toggleMeadArchiveFlag(String meadId)
    {
        Log.i("JavaScriptBridge", "Toggling archive flag for mead id: " + meadId);

        Mead mead = data.getMead(parseInt(meadId));

        data.setMeadArchiveFlag(mead.getId(), !mead.getArchived());
    }

    @JavascriptInterface
    public void addReading(String meadId, String date, String specificGravity) {

        Log.d("JavaScriptBridge", "Adding reading record for: " + meadId);

        Reading reading = new Reading();

        reading.setMeadId(parseInt(meadId));
        reading.setDate(date);
        reading.setSpecificGravity(specificGravity);

        data.addReading(reading);
    }

    @JavascriptInterface
    public String fetchEvent(String id)
    {
        Log.i("JavaScriptBridge", "Fetching event data by ID: " + id);

        Event event = data.getEvent(parseInt(id));

        Gson gson = new Gson();

        String json = gson.toJson(event);

        Log.d("JavaScriptBridge", json);

        return json;
    }

    @JavascriptInterface
    public void addEvent(String meadId, String date, String typeId, String description) {

        Log.d("JavaScriptBridge", "Adding log entry record for: " + meadId);

        Event event = new Event();

        event.setMeadId(parseInt(meadId));
        event.setDate(date);
        event.setTypeId(parseInt(typeId));
        event.setDescription(description);

        data.addEvent(event);
    }

    @JavascriptInterface
    public void updateEvent(String eventId, String meadId, String date, String typeId, String description) {

        Log.d("JavaScriptBridge", "Updating event record for event ID: " + eventId);

        Event event = new Event();

        event.setId(parseInt(eventId));
        event.setMeadId(parseInt(meadId));
        event.setDate(date);
        event.setTypeId(parseInt(typeId));
        event.setDescription(description);

        data.updateEvent(event);
    }

    @JavascriptInterface
    public void deleteMead(String id)
    {
        Log.i("JavaScriptBridge", "Deleting mead by id: " + id);

        data.deleteMead(parseInt(id));
    }

    @JavascriptInterface
    public void deleteMeadTag(String meadId, String tagName)
    {
        Log.i("JavaScriptBridge", "Deleting mead tag by name " + tagName + " off of mead " + meadId);

        data.deleteMeadTag(parseInt(meadId), tagName);
    }

    @JavascriptInterface
    public void deleteReading(String id)
    {
        Log.i("JavaScriptBridge", "Deleting reading by id: " + id);

        data.deleteReading(parseInt(id));
    }

    @JavascriptInterface
    public void deleteEvent(String id)
    {
        Log.i("JavaScriptBridge", "Deleting event by id: " + id);

        data.deleteEvent(parseInt(id));
    }

    @JavascriptInterface
    public void splitMead(String meadId, String splitCount, boolean deleteOriginal)
    {
        try
        {
            Log.d("splitMead", "Mead ID: " + meadId);
            Log.d("splitMead", "Count: " + splitCount);
            Log.d("splitMead", "Delete Original: " + deleteOriginal);

            int meadRecordId = parseInt(meadId);
            int count = parseInt(splitCount);
            boolean canBeDeleted = deleteOriginal;

            // Minor validation
            if(count < 2)
            {
                return; // this shouldn't happen, but covering the base
            }

            data.splitMead(meadRecordId, count, canBeDeleted);
        }
        catch(Exception ex)
        {
            Log.e("splitMead", ex.toString());
        }
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

    @JavascriptInterface
    public String versionInfo()
    {
        ApplicationInfo model = new ApplicationInfo();

        try
        {
            Log.i("JavaScriptBridge", "Fetching version info");

            PackageInfo pinfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);

            LocalDate updateDate = Instant.ofEpochMilli(pinfo.lastUpdateTime).atZone(ZoneId.systemDefault()).toLocalDate();

            model.setDatabaseVersion(MeadMateData.getDbVersion());
            model.setVersionName(pinfo.versionName);
            model.setVersionNumber(pinfo.versionCode);
            model.setDateUpdated(updateDate.toString());

            Gson gson = new Gson();
            String json = gson.toJson(model);

            Log.d("JavaScriptBridge", json);

            return json;
        }
        catch(Exception ex)
        {
            Log.e("JavaScriptBridge", ex.toString());

            model.setVersionNumber(0);
            model.setVersionName("ERROR");
            model.setDatabaseVersion(0);

            Gson gson = new Gson();
            String json = gson.toJson(model);

            return json;
        }
    }
}