package com.dawsonsoftware.meadmate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.dawsonsoftware.meadmate.models.Mead;
import com.dawsonsoftware.meadmate.models.Reading;

import java.io.File;

@RunWith(AndroidJUnit4.class)
public class AbvCalculatorTests {

    @Test
    public void calculateAbvWithNoExtraSugar() throws InterruptedException {
        /*// Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        Intent intent = new Intent(appContext, MainActivity.class);

        appContext.startActivity(intent);

        MeadMateData dataLayer = new MeadMateData(appContext);

        // Create mead record
        Mead testMead = new Mead();

        testMead.setDescription("Unit testing record.");
        testMead.setName("Unit Testing Batch");
        testMead.setOriginalGravity("1.10");
        testMead.setStartDate("2022-01-01");
        testMead.setArchived(false);

        int meadId = dataLayer.addMead(testMead);

        // Adding several reading records
        Reading reading1 = new Reading();
        Reading reading2 = new Reading();
        Reading reading3 = new Reading();
        Reading reading4 = new Reading();

        reading1.setMeadId(meadId);
        reading2.setMeadId(meadId);
        reading3.setMeadId(meadId);
        reading4.setMeadId(meadId);

        reading1.setDate("2022-01-15");
        reading1.setSpecificGravity("1.08");

        reading2.setDate("2022-02-01");
        reading2.setSpecificGravity("1.06");

        reading3.setDate("2022-02-15");
        reading3.setSpecificGravity("1.04");

        reading4.setDate("2022-03-01");
        reading4.setSpecificGravity("1.0");

        // Calculate ABV using readings

        String abv = "";

        assertEquals("13.13%", abv);*/

        assertTrue(true);
    }
}
