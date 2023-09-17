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

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.CalendarContract;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.dawsonsoftware.meadmate.models.CombinedMeadRecord;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private final MeadMateData data = new MeadMateData(this);
    private static final int CREATE_CSV_FILE = 1;
    private static final int CREATE_JSON_FILE = 2;
    private static final int CREATE_EVENT = 3;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.activity_main_webview);

        // Force links and redirects to open in the WebView instead of in a browser
        //mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // Establish JS to Java method access
        mWebView.addJavascriptInterface(new JavaScriptBridge(this), "Android");

        // LOCAL RESOURCE
        mWebView.loadUrl(getString(R.string.base_url));
    }

    // Prevent the back-button from closing the app
    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public void requestEvent(EventRequest request)
    {
        try
        {
            TimeZone defaultTimeZone = TimeZone.getDefault();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(request.getDate(), formatter);
            LocalDate endDate = startDate.plusDays(1);

            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atStartOfDay();

            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setType("vnd.android.cursor.dir/event");
            Log.d("MainActivity", "Attempting to start activity...");

            intent.setData(CalendarContract.Events.CONTENT_URI);
            intent.putExtra(CalendarContract.Events.CALENDAR_ID, 1);
            intent.putExtra(CalendarContract.Events.TITLE, request.getTitle());
            intent.putExtra(CalendarContract.Events.DESCRIPTION, request.getDescription());
            intent.putExtra(CalendarContract.Events.ALL_DAY, true);
            intent.putExtra(CalendarContract.Events.EVENT_TIMEZONE, defaultTimeZone.getID());
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDateTime.atZone(defaultTimeZone.toZoneId()).toInstant().toEpochMilli());
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDateTime.atZone(defaultTimeZone.toZoneId()).toInstant().toEpochMilli());

            ComponentName componentName = intent.resolveActivity(this.getPackageManager());

            if(componentName == null)
            {
                Context context = getApplicationContext();
                CharSequence text = "No scheduling app found";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                return;
            }

            startActivityForResult(intent, CREATE_EVENT);
        }
        catch(Exception ex)
        {
            Log.e("MainActivity", ex.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        super.onActivityResult(requestCode, resultCode, resultData);

        if(requestCode == CREATE_EVENT)
        {
            Log.d("MainActivity", "Create Event ResultCode: " + resultCode);
        }

        if(requestCode == CREATE_CSV_FILE && resultCode == Activity.RESULT_OK)
        {
            Log.d("MainActivity", "Create Event ResultCode: " + resultCode);

            Uri uri = null;

            if (resultData != null) {

                uri = resultData.getData();

                Log.d("MainActivity", "Selected file path: " + uri);

                try
                {
                    String content = generateCsvExportFile();
                    writeExportFile(uri, content);
                }
                catch(Exception ex)
                {
                    Log.e("MainActivity", "Unexpected error while exporting batch data to CSV.", ex);
                }
            }
        }

        if(requestCode == CREATE_JSON_FILE && resultCode == Activity.RESULT_OK)
        {
            Log.d("MainActivity", "Create Event ResultCode: " + resultCode);

            Uri uri = null;

            if (resultData != null) {

                uri = resultData.getData();

                Log.d("MainActivity", "Selected file path: " + uri);

                try
                {
                    String content = generateJsonExportFile();
                    writeExportFile(uri, content);
                }
                catch(Exception ex)
                {
                    Log.e("MainActivity", "Unexpected error while exporting batch data to JSON.", ex);
                }
            }
        }
    }

    public void changeTheme(String themeCode)
    {
        if(themeCode == null)
        {
            Log.e("changeTheme", "Null theme code. Ignoring.");
            return;
        }

        Log.i("changeTheme", "Theme change requested to themeCode: '" + themeCode + "'.");

        mWebView = findViewById(R.id.activity_main_webview);

        if(themeCode.equals("a"))
        {
            mWebView.post(() -> mWebView.loadUrl(getString(R.string.theme_a_url)));
            return;
        }

        if(themeCode.equals("b"))
        {
            mWebView.post(() -> mWebView.loadUrl(getString(R.string.theme_b_url)));
            return;
        }
    }

    public void requestCsvExportFileUri() {

        Log.i("DataExport", "Starting mead data export in CSV.");

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, "mead-mate-data.csv");

        startActivityForResult(intent, CREATE_CSV_FILE);
    }

    public void requestJsonExportFileUri() {

        Log.i("DataExport", "Starting mead data export in JSON.");

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, "mead-mate-data.json");

        startActivityForResult(intent, CREATE_JSON_FILE);
    }

    private String generateCsvExportFile() {

        try {

            List<CombinedMeadRecord> records = data.getMeadRecords();

            StringBuilder str = new StringBuilder();

            // header
            str.append("MeadId,");
            str.append("BatchName,");
            str.append("Start Date,");
            str.append("Description,");
            str.append("Starting Gravity,");
            str.append("Archived,");
            str.append("Tags,");
            str.append("Event Date,");
            str.append("Event Type,");
            str.append("Event Description / Value" + System.lineSeparator());

            for (CombinedMeadRecord record : records) {

                // Remove \r \n \t from Description and Event Description

                String description = record.getDescription();
                String eventDescription = record.getEventDescription();

                if(description != null) {
                    description = description.replaceAll("\\r", " ");
                    description = description.replaceAll("\\n", " ");
                    description = description.replaceAll("\\t", " ");
                }

                if(eventDescription != null){
                    eventDescription = eventDescription.replaceAll("\\r", " ");
                    eventDescription = eventDescription.replaceAll("\\n", " ");
                    eventDescription = eventDescription.replaceAll("\\t", " ");
                }

                str.append(record.getMeadId() + ",");
                str.append(record.getBatchName() + ",");
                str.append(record.getStartDate() + ",");
                str.append(description + ",");
                str.append(record.getStartingGravity() + ",");
                str.append(record.getArchived() + ",");
                str.append(record.getTags() + ",");
                str.append(record.getEventDate() + ",");
                str.append(record.getEventType() + ",");
                str.append(eventDescription + System.lineSeparator());
            }

            return str.toString();

        } catch (Exception e) {

            Log.e("MainActivity", "Unexpected error in generateCsvExportFile", e);

            return "Unexpected error while trying to export data.";
        }
    }

    private String generateJsonExportFile()
    {
        try {

            List<CombinedMeadRecord> records = data.getMeadRecords();

            Gson gson = new Gson();

            return gson.toJson(records);

        } catch (Exception e) {

            Log.e("MainActivity", "Unexpected error in generateJsonExportFile", e);

            return "Unexpected error while trying to export data.";
        }
    }

    private void writeExportFile(Uri uri, String content)
    {
        try
        {
            ParcelFileDescriptor pfd = this.getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
            fileOutputStream.write(content.getBytes());
            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();
        }
        catch(Exception e)
        {
            Log.e("MainActivity", "Unexpected error in writeExportFile", e);
        }
    }
}