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
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.identity.AuthorizationRequest;
import com.google.android.gms.auth.api.identity.AuthorizationResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.common.api.Scope;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Google's OAuth 2.0 client for this app must be configured in Google Cloud Console
 * (Drive API enabled, OAuth consent screen, Android OAuth client ID with this app's
 * SHA-1 fingerprint) before authorize() will succeed. See the project plan for setup steps.
 */
public class DriveBackupManager {

    private static final String TAG = "DriveBackupManager";
    private static final String DRIVE_FILE_SCOPE = "https://www.googleapis.com/auth/drive.file";
    private static final String BACKUP_FOLDER_NAME = "Mead Mate Backups";
    private static final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder";
    private static final String DRIVE_FILES_URL = "https://www.googleapis.com/drive/v3/files";
    private static final String DRIVE_UPLOAD_URL = "https://www.googleapis.com/upload/drive/v3/files/";

    private final Activity activity;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Gson gson = new Gson();

    public interface AuthorizationCallback {
        void onAuthorized(String accessToken);
        void onResolutionRequired(PendingIntent pendingIntent);
        void onFailure(Exception exception);
    }

    public interface UploadCallback {
        void onSuccess(String fileName);
        void onFailure(String message);
    }

    public DriveBackupManager(Activity activity) {
        this.activity = activity;
    }

    public void authorize(AuthorizationCallback callback) {
        AuthorizationRequest request = AuthorizationRequest.builder()
                .setRequestedScopes(Collections.singletonList(new Scope(DRIVE_FILE_SCOPE)))
                .build();

        Identity.getAuthorizationClient(activity)
                .authorize(request)
                .addOnSuccessListener(result -> {
                    if (result.hasResolution()) {
                        callback.onResolutionRequired(result.getPendingIntent());
                    } else {
                        callback.onAuthorized(result.getAccessToken());
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void handleAuthorizationResult(Intent data, AuthorizationCallback callback) {
        try {
            AuthorizationResult result = Identity.getAuthorizationClient(activity)
                    .getAuthorizationResultFromIntent(data);
            callback.onAuthorized(result.getAccessToken());
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    public void uploadBackup(String accessToken, String jsonContent, UploadCallback callback) {
        executor.execute(() -> {
            try {
                String folderId = findOrCreateBackupFolder(accessToken);
                String fileName = "mead-mate-data-" +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HHmmss")) + ".json";
                String fileId = createFileMetadata(accessToken, fileName, folderId);
                uploadFileContent(accessToken, fileId, jsonContent);

                activity.runOnUiThread(() -> callback.onSuccess(fileName));
            } catch (Exception e) {
                Log.e(TAG, "Drive backup failed", e);
                activity.runOnUiThread(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    private String findOrCreateBackupFolder(String accessToken) throws IOException {
        String query = "mimeType='" + FOLDER_MIME_TYPE + "' and name='" + BACKUP_FOLDER_NAME + "' and trashed=false";
        String url = DRIVE_FILES_URL + "?q=" + URLEncoder.encode(query, "UTF-8") + "&spaces=drive&fields=files(id,name)";

        JsonObject response = gson.fromJson(get(url, accessToken), JsonObject.class);
        JsonArray files = response.getAsJsonArray("files");

        if (files != null && files.size() > 0) {
            return files.get(0).getAsJsonObject().get("id").getAsString();
        }

        JsonObject metadata = new JsonObject();
        metadata.addProperty("name", BACKUP_FOLDER_NAME);
        metadata.addProperty("mimeType", FOLDER_MIME_TYPE);

        JsonObject created = gson.fromJson(post(DRIVE_FILES_URL, accessToken, metadata.toString()), JsonObject.class);
        return created.get("id").getAsString();
    }

    private String createFileMetadata(String accessToken, String fileName, String folderId) throws IOException {
        JsonObject metadata = new JsonObject();
        metadata.addProperty("name", fileName);
        JsonArray parents = new JsonArray();
        parents.add(folderId);
        metadata.add("parents", parents);

        JsonObject created = gson.fromJson(post(DRIVE_FILES_URL, accessToken, metadata.toString()), JsonObject.class);
        return created.get("id").getAsString();
    }

    private void uploadFileContent(String accessToken, String fileId, String content) throws IOException {
        String url = DRIVE_UPLOAD_URL + fileId + "?uploadType=media";
        patchMedia(url, accessToken, content);
    }

    private String get(String urlString, String accessToken) throws IOException {
        HttpURLConnection connection = openConnection(urlString, "GET", accessToken);
        return readResponse(connection);
    }

    private String post(String urlString, String accessToken, String jsonBody) throws IOException {
        HttpURLConnection connection = openConnection(urlString, "POST", accessToken);
        writeBody(connection, jsonBody, "application/json; charset=UTF-8");
        return readResponse(connection);
    }

    // Drive's media-update endpoint expects PATCH; HttpURLConnection's method allow-list
    // varies by platform, so the update is sent as a POST with Google's documented
    // X-HTTP-Method-Override header instead of relying on literal PATCH support.
    private String patchMedia(String urlString, String accessToken, String jsonBody) throws IOException {
        HttpURLConnection connection = openConnection(urlString, "POST", accessToken);
        connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
        writeBody(connection, jsonBody, "application/json; charset=UTF-8");
        return readResponse(connection);
    }

    private HttpURLConnection openConnection(String urlString, String method, String accessToken) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        return connection;
    }

    private void writeBody(HttpURLConnection connection, String body, String contentType) throws IOException {
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", contentType);
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        connection.setFixedLengthStreamingMode(bytes.length);
        try (OutputStream out = connection.getOutputStream()) {
            out.write(bytes);
        }
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        int status = connection.getResponseCode();
        boolean ok = status >= 200 && status < 300;
        InputStream stream = ok ? connection.getInputStream() : connection.getErrorStream();

        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }

        if (!ok) {
            throw new IOException("Drive API request failed with status " + status + ": " + builder);
        }

        return builder.toString();
    }
}
