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
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;

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
        mWebView.loadUrl("file:///android_asset/index.html");
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

            startActivityForResult(intent, 1);
        }
        catch(Exception ex)
        {
            Log.e("MainActivity", ex.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1)
        {
            Log.d("MainActivity", "ResultCode: " + resultCode);

            String functionSig = "calendarEventCallback(" + resultCode + ")";

            mWebView.loadUrl("javascript:" + functionSig);
        }
    }
}