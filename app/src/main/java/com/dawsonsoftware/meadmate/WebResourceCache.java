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

    public WebResourceCache(Context context)
    {
        if(context == null)
        {
            throw new IllegalArgumentException("The context is required for this class to function.");
        }

        _cacheDir = context.getCacheDir();
    }

    public static String RetrieveWebContent(String sUrl, int maxAgeSeconds)
    {
        if(sUrl.isEmpty())
        {
            Log.e("Retrieve", "RetrieveWebContent: sUrl cannot be empty");
            return null;
        }

        int maxAge = (maxAgeSeconds >= 0) ? maxAgeSeconds : 300; // 5 minutes

        String fileContents = fetchCachedFileContents(sUrl, maxAge);

        if(fileContents == null || fileContents.isEmpty())
        {
            fileContents = fetchOnlineFileContents(sUrl);
        }

        if(fileContents == null || fileContents.isEmpty())
        {
            return null;
        }

        cacheOnlineFileContents(sUrl, fileContents);

        return fileContents;
    }

    private static void cacheOnlineFileContents(String sUrl, String fileContents)
    {
        try
        {
            // Calculate checksum on sUrl
            long checksum = getChecksum(sUrl.getBytes());

            // Convert checksum to string, add expected extension
            String filename = checksum + ".json";

            // Create absolute path to possible cached file
            Path path = Paths.get(_cacheDir.getPath(), filename);

            byte[] strToBytes = fileContents.getBytes();

            Files.write(path, strToBytes);
        }
        catch(Exception ex)
        {
            //TODO: log error
        }
    }

    private static String fetchOnlineFileContents(String sUrl)
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

        }

        return null;
    }

    private static String fetchCachedFileContents(String sUrl, int maxAge)
    {
        // Calculate checksum on sUrl
        long checksum = getChecksum(sUrl.getBytes());

        // Convert checksum to string, add expected extension
        String filename = checksum + ".json";

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

                if((now - fileAge) < maxAge)
                {
                    return new String (Files.readAllBytes(path));
                }
            }
            catch (IOException e)
            {
                //TODO: log error
            }
        }

        return null;
    }

    private static long getChecksum(@NonNull byte[] bytes) {

        Log.d("getChecksum", "Calculating checksum for " + bytes.length + " byte array.");

        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);

        long value = crc32.getValue();

        Log.d("getChecksum", "Checksum: " + value);

        return value;
    }

}
