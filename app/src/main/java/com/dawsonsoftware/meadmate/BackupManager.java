package com.dawsonsoftware.meadmate;

import android.content.Context;
import com.dawsonsoftware.meadmate.backupmodels.*;
import com.dawsonsoftware.meadmate.models.*;

import java.util.List;

public class BackupManager {

    //private MeadMateBackup _backupModel;
    private final MeadMateData meadData;

    public BackupManager(Context context)
    {
        //_backupModel = new MeadMateBackup();
        meadData = new MeadMateData(context);
    }
    public MeadMateBackup CreateBackup()
    {
        MeadMateBackup backupModel = new MeadMateBackup();

        // Fetch mead & recipe data
        List<Mead> meads = meadData.getMeads(null, true);
        List<Recipe> recipes = meadData.getRecipes();
        
        // Add mead data and related entities to backup model
        for (Mead meadItem: meads)
        {
            MeadRecord meadRecord = new MeadRecord(meadItem);
            backupModel.getMeadRecords().add(meadRecord);

            // Fetch event data
            List<Event> events = meadData.getEvents(meadRecord.getId());

            for(Event eventItem: events)
            {
                meadRecord.getEvents().add(eventItem);
            }

            // Fetch reading data
            List<Reading> readings = meadData.getReadings(meadRecord.getId());

            for(Reading readingItem: readings)
            {
                meadRecord.getReadings().add(readingItem);
            }

            List<Tag> tags = meadData.getMeadTags(meadRecord.getId());

            for(Tag tagItem: tags)
            {
                meadRecord.getTags().add(tagItem.getName());
            }
        }

        for (Recipe recipeItem: recipes)
        {
            backupModel.getRecipes().add(recipeItem);
        }

        return backupModel;
    }
}
