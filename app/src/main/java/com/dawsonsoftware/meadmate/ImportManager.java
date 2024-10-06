package com.dawsonsoftware.meadmate;

import android.content.Context;
import android.util.Log;

import com.dawsonsoftware.meadmate.backupmodels.MeadMateBackup;
import com.dawsonsoftware.meadmate.backupmodels.MeadRecord;
import com.dawsonsoftware.meadmate.models.Event;
import com.dawsonsoftware.meadmate.models.Reading;
import com.dawsonsoftware.meadmate.models.Recipe;

public class ImportManager {

    private final MeadMateData meadData;

    public ImportManager(Context context)
    {
        //_backupModel = new MeadMateBackup();
        meadData = new MeadMateData(context);
    }

    public void ImportMeadData(MeadMateBackup data)
    {
        if(data == null)
        {
            Log.e("ImportManager", "Cannot import data. Parameter is null.");
            return;
        }

        Log.d("ImportManager", String.format("Importing %s mead records.", data.getMeadRecords().size()));
        Log.d("ImportManager", String.format("Importing %s recipe records.", data.getRecipes().size()));

        try
        {
            // Mead batches
            for (MeadRecord mead : data.getMeadRecords())
            {
                int meadId = meadData.addMead(mead);

                for(Event event : mead.getEvents())
                {
                    // Override the exported mead ID with the newly imported one
                    event.setMeadId(meadId);
                    meadData.addEvent(event);
                }

                for(Reading reading : mead.getReadings())
                {
                    // Override the exported mead ID with the newly imported one
                    reading.setMeadId(meadId);
                    meadData.addReading(reading);
                }

                for(String tag : mead.getTags())
                {
                    int tagId = meadData.addTag(tag);
                    meadData.addMeadTag(meadId, tagId);
                }
            }

            // Recipes
            for (Recipe recipe : data.getRecipes()) {
                meadData.addRecipe(recipe);
            }
        }
        catch(Exception ex)
        {
            Log.e("ImportManager", "An error occurred while importing data.", ex);
        }
    }
}
