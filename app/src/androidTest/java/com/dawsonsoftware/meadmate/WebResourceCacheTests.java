package com.dawsonsoftware.meadmate;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.io.File;

@RunWith(AndroidJUnit4.class)
public class WebResourceCacheTests {

    private final String _testUrl = "https://storage.googleapis.com/dawson-software/static/meadmate/about/test-file.json";
    private final String _missingUrl = "https://storage.googleapis.com/dawson-software/static/meadmate/about/missing.json";

    @Test
    public void retrieveHostedFileSuccessfully() throws InterruptedException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        WebResourceCache cache = new WebResourceCache(appContext);

        String testUrl = _testUrl;

        String data = cache.retrieveWebContent(testUrl, -1);

        assertNotNull(data);
    }

    @Test
    public void handleMissingHostedFileGracefully() throws InterruptedException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        WebResourceCache cache = new WebResourceCache(appContext);

        String testUrl = _missingUrl;

        String data = cache.retrieveWebContent(testUrl, -1);

        assertNull(data);
    }

    @Test
    public void verifyLocalCachedFiles()
    {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        File _cacheDir = appContext.getCacheDir();

        String[] children = _cacheDir.list();

        for (int i = 0; i < children.length; i++) {
            File child = new File(_cacheDir, children[i]);

            if(child.isFile())
            {
                try
                {
                    child.delete();
                    Log.d("UnitTests", "Deleted file from app cache.");
                }
                catch(Exception ex)
                {
                    Log.e("UnitTests", ex.toString());
                }
            }
        }

        int preCachedFileCount = _cacheDir.list().length;

        WebResourceCache cache = new WebResourceCache(appContext);

        String testUrl = _testUrl;

        String data = cache.retrieveWebContent(testUrl, -1);

        int postCachedFileCount = _cacheDir.list().length;

        assert(postCachedFileCount == preCachedFileCount + 1);
    }
}
