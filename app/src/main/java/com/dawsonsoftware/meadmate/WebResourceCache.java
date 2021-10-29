package com.dawsonsoftware.meadmate;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class WebResourceCache {

    private WebResourceCache()
    {

    }

    public static String RetrieveWebContent(String resource, int maxAgeSeconds) throws IOException
    {
        return ""; //TODO: not even close to done

        // Connect to the URL using java's native library
        URL url = new URL(resource);
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

}
