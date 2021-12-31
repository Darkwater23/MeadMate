package com.dawsonsoftware.meadmate;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public final class WebResourceCache {

    private static File _cacheDir;
    private final static String _cacheFileExtension = ".data";
    private final Checksum _crc32 = new CRC32();
    private final int _defaultCacheTimeout = 500;

    public WebResourceCache(Context context)
    {
        if(context == null)
        {
            throw new IllegalArgumentException("The context is required for this class to function.");
        }

        _cacheDir = context.getCacheDir();
    }

    public String retrieveWebContent(String sUrl, int maxAgeSeconds)
    {
        if(sUrl.isEmpty())
        {
            Log.e("WebCache", "RetrieveWebContent: sUrl cannot be empty");
            return null;
        }

        int maxAge = (maxAgeSeconds >= 0) ? maxAgeSeconds : _defaultCacheTimeout; // 5 minutes

        Log.d("WebCache", "MaxAge set to: " + maxAge);

        String fileContents = fetchCachedFileContents(sUrl, maxAge);

        if(fileContents == null || fileContents.isEmpty())
        {
            Log.d("WebCache", "File not present in cache. Fetching from host.");
            fileContents = fetchOnlineFileContents(sUrl);
        }

        if(fileContents == null || fileContents.isEmpty())
        {
            Log.d("WebCache", "File not found on host. URL: " + sUrl);
            return null;
        }
        else
        {
            Log.d("WebCache", "File retrieved from host. Writing to local app cache.");
            cacheOnlineFileContents(sUrl, fileContents);
        }

        return fileContents;
    }

    private void cacheOnlineFileContents(String sUrl, String fileContents)
    {
        try
        {
            // Calculate checksum on sUrl
            long checksum = getChecksum(sUrl.getBytes());

            // Convert checksum to string, add generic extension
            String filename = checksum + _cacheFileExtension;

            // Create absolute path to possible cached file
            Path path = Paths.get(_cacheDir.getPath(), filename);

            byte[] strToBytes = fileContents.getBytes();

            Files.write(path, strToBytes);

            Log.d("WebCache", "Cached web content to: " + path.toString());
        }
        catch(Exception ex)
        {
            Log.e("WebCache", ex.toString());
        }
    }

    private String fetchOnlineFileContents(String sUrl)
    {
        try
        {
            // Connect to the URL using java's native library
            URL url = new URL(sUrl);
            URLConnection conn = url.openConnection();
            conn.connect();

            InputStreamReader reader = new InputStreamReader((InputStream) conn.getContent());
            //Creating a character array
            char charArray[] = new char[conn.getContentLength()];

            //Reading the contents of the reader
            reader.read(charArray);

            //Converting character array to a String
            return new String(charArray);
        }
        catch (Exception ex)
        {
            Log.e("WebCache", ex.toString());
        }

        return null;
    }

    private String fetchCachedFileContents(String sUrl, int maxAge)
    {
        // Calculate checksum on sUrl
        long checksum = getChecksum(sUrl.getBytes());

        // Convert checksum to string, add expected extension
        String filename = checksum + _cacheFileExtension;

        // Create absolute path to possible cached file
        Path path = Paths.get(_cacheDir.getPath(), filename);

        // Check if local cache file exists with that name
        File file = path.toFile();

        if(file.exists())
        {
            try
            {
                long fileAge = file.lastModified();

                long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

                Log.d("WebCache", "FileAge: " + String.valueOf(fileAge));
                Log.d("WebCache", "Now: " + String.valueOf(now));
                Log.d("WebCache", "MaxAge: " + String.valueOf(maxAge));

                if((now - fileAge) < maxAge)
                {
                    Log.d("WebCache", "Returned file from cache. " + path.toString());
                    return new String (Files.readAllBytes(path));
                }
            }
            catch (IOException ex)
            {
                Log.e("WebCache", ex.toString());
            }
        }

        return null;
    }

    private long getChecksum(@NonNull byte[] bytes) {

        Log.d("WebCache", "Calculating checksum for " + bytes.length + " byte array.");

        _crc32.update(bytes, 0, bytes.length);

        long value = _crc32.getValue();

        Log.d("WebCache", "Checksum: " + value);

        return value;
    }

}
