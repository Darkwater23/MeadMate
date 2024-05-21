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

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dawsonsoftware.meadmate.models.CombinedMeadRecord;
import com.dawsonsoftware.meadmate.models.Event;
import com.dawsonsoftware.meadmate.models.EventType;
import com.dawsonsoftware.meadmate.models.Mead;
import com.dawsonsoftware.meadmate.models.Recipe;
import com.dawsonsoftware.meadmate.models.Reading;
import com.dawsonsoftware.meadmate.models.Tag;

import java.util.ArrayList;
import java.util.List;

public class MeadMateData extends SQLiteOpenHelper {

    private static final int DB_VERSION = 9;
    private static final String DB_NAME = "appdata";

    // Meads table fields
    private static final String TABLE_MEADS = "MEADS";
    private static final String KEY_MEAD_ID = "_ID";
    private static final String KEY_MEAD_NAME = "NAME";
    private static final String KEY_MEAD_START_DATE = "START_DATE";
    private static final String KEY_MEAD_DESC = "DESCRIPTION";
    private static final String KEY_MEAD_ORIG_GRAV = "ORIGINAL_GRAVITY";
    private static final String KEY_MEAD_ARCHIVED = "ARCHIVED";

    // Mead Events table fields
    private static final String TABLE_EVENTS = "EVENTS";
    private static final String KEY_EVENT_ID = "_ID";
    private static final String KEY_EVENT_MEADID = "MEAD_ID";
    private static final String KEY_EVENT_DATE = "DATE";
    private static final String KEY_EVENT_TYPEID = "TYPE_ID";
    private static final String KEY_EVENT_DESC = "DESCRIPTION";

    // Mead Event Types table fields
    private static final String TABLE_EVENT_TYPES = "EVENT_TYPES";
    private static final String KEY_EVENT_TYPE_ID = "_ID";
    private static final String KEY_EVENT_TYPE_NAME = "NAME";

    // Readings table fields
    private static final String TABLE_READINGS = "READINGS";
    private static final String KEY_READINGS_ID = "_ID";
    private static final String KEY_READINGS_MEADID = "MEAD_ID";
    private static final String KEY_READINGS_DATE = "DATE";
    private static final String KEY_READINGS_GRAV = "SPECIFIC_GRAVITY";

    // Tags table fields
    private static final String TABLE_TAGS = "TAGS";
    private static final String KEY_TAGS_ID = "_ID";
    private static final String KEY_TAGS_NAME = "NAME";

    // MeadTags table fields
    private static final String TABLE_MEAD_TAGS = "MEAD_TAGS";
    private static final String KEY_MEAD_TAGS_ID = "_ID";
    private static final String KEY_MEAD_TAGS_MEAD_ID = "MEAD_ID";
    private static final String KEY_MEAD_TAGS_TAG_ID = "TAG_ID";

    // Recipes table fields
    private static final String TABLE_RECIPES = "RECIPES";
    private static final String KEY_RECIPE_ID = "_ID";
    private static final String KEY_RECIPE_NAME = "NAME";
    private static final String KEY_RECIPE_DESC = "DESCRIPTION";

    // Table creation strings
    String CREATE_MEAD_TABLE = "CREATE TABLE " + TABLE_MEADS + " (" +
            KEY_MEAD_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
            KEY_MEAD_NAME + " TEXT NOT NULL," +
            KEY_MEAD_START_DATE + " TEXT NOT NULL," +
            KEY_MEAD_DESC + " TEXT," +
            KEY_MEAD_ORIG_GRAV + " TEXT NOT NULL DEFAULT '0.0'," +
            KEY_MEAD_ARCHIVED + " INTEGER NOT NULL DEFAULT 0)";

    String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + " (" +
            KEY_EVENT_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
            KEY_EVENT_MEADID + " INTEGER NOT NULL," +
            KEY_EVENT_DATE + " TEXT NOT NULL, " +
            KEY_EVENT_TYPEID + " INTEGER NOT NULL, " +
            KEY_EVENT_DESC + " TEXT NOT NULL)";

    String CREATE_EVENT_TYPES_TABLE = "CREATE TABLE " + TABLE_EVENT_TYPES + " (" +
            KEY_EVENT_TYPE_ID + " INTEGER NOT NULL PRIMARY KEY UNIQUE," +
            KEY_EVENT_TYPE_NAME + " TEXT NOT NULL)";

    String LOAD_EVENT_TYPES_TABLE = "INSERT INTO " + TABLE_EVENT_TYPES + " (" +
            KEY_EVENT_TYPE_ID + "," + KEY_EVENT_TYPE_NAME + ") VALUES " +
            "(1,'Primary Fermentation')," +
            "(2,'Secondary Fermentation')," +
            "(3,'Racked')," +
            "(4,'Bottled')," +
            "(5,'Discarded')," +
            "(6,'Tasting')," +
            "(7,'Backsweetened')," +
            "(8,'Note')," +
            "(9,'Conditioning')," +
            "(10,'Feeding')," +
            "(11,'Cold Crashing')";

    String ADD_NEW_EVENT_TYPES = "INSERT INTO " + TABLE_EVENT_TYPES + " (" +
            KEY_EVENT_TYPE_ID + "," + KEY_EVENT_TYPE_NAME + ") VALUES " +
            "(7,'Backsweetened')," +
            "(8,'Note')";

    String ADD_NEW_EVENT_TYPE_REL6 = "INSERT INTO " + TABLE_EVENT_TYPES + " (" +
            KEY_EVENT_TYPE_ID + "," + KEY_EVENT_TYPE_NAME + ") VALUES " +
            "(9,'Conditioning')";

    String CREATE_READINGS_TABLE = "CREATE TABLE " + TABLE_READINGS + " (" +
            KEY_READINGS_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
            KEY_READINGS_MEADID + " INTEGER NOT NULL," +
            KEY_READINGS_DATE + " TEXT NOT NULL," +
            KEY_READINGS_GRAV + " TEXT NOT NULL)";

    String CREATE_TAGS_TABLE = "CREATE TABLE " + TABLE_TAGS + " (" +
            KEY_TAGS_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
            KEY_TAGS_NAME + " TEXT NOT NULL)";

    String CREATE_MEAD_TAGS_TABLE = "CREATE TABLE " + TABLE_MEAD_TAGS + " (" +
            KEY_MEAD_TAGS_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
            KEY_MEAD_TAGS_MEAD_ID + " INTEGER NOT NULL," +
            KEY_MEAD_TAGS_TAG_ID + " INTEGER NOT NULL)";

    String ALTER_MEAD_TABLE = "ALTER TABLE " + TABLE_MEADS +
            " ADD COLUMN " + KEY_MEAD_ARCHIVED + " INTEGER NOT NULL DEFAULT 0";

    String CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_RECIPES + " (" +
            KEY_RECIPE_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
            KEY_RECIPE_NAME + " TEXT NOT NULL," +
            KEY_RECIPE_DESC + " TEXT NOT NULL)";

    String ADD_NEW_EVENT_TYPE_REL8 = "INSERT INTO " + TABLE_EVENT_TYPES + " (" +
            KEY_EVENT_TYPE_ID + "," + KEY_EVENT_TYPE_NAME + ") VALUES " +
            "(10,'Feeding')";

    String ADD_NEW_EVENT_TYPE_REL12 = "INSERT INTO " + TABLE_EVENT_TYPES + " (" +
            KEY_EVENT_TYPE_ID + "," + KEY_EVENT_TYPE_NAME + ") VALUES " +
            "(11,'Cold Crashing')";

    String FIX_BAD_MEADS_DATE_DATA = "UPDATE MEADS SET START_DATE = substr(START_DATE, 7, 4) || '-' || substr(START_DATE, 1, 2) || '-' || substr(START_DATE, 4, 2) WHERE START_DATE LIKE '%/%'";

    String FIX_BAD_EVENTS_DATE_DATA = "UPDATE EVENTS SET \"DATE\" = substr(\"DATE\", 7, 4) || '-' || substr(\"DATE\", 1, 2) || '-' || substr(\"DATE\", 4, 2) WHERE \"DATE\" LIKE '%/%'";

    String FIX_BAD_READINGS_DATE_DATA = "UPDATE READINGS SET \"DATE\" = substr(\"DATE\", 7, 4) || '-' || substr(\"DATE\", 1, 2) || '-' || substr(\"DATE\", 4, 2) WHERE \"DATE\" LIKE '%/%'";

    public MeadMateData(Context context){
        super(context,DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.i(MeadMateData.class.getTypeName(), "Creating database...");

        db.execSQL(CREATE_MEAD_TABLE);
        db.execSQL(CREATE_EVENTS_TABLE);
        db.execSQL(CREATE_EVENT_TYPES_TABLE);
        db.execSQL(LOAD_EVENT_TYPES_TABLE);
        db.execSQL(CREATE_READINGS_TABLE);
        db.execSQL(CREATE_TAGS_TABLE);
        db.execSQL(CREATE_MEAD_TAGS_TABLE);
        db.execSQL(CREATE_RECIPES_TABLE);

        Log.i(MeadMateData.class.getTypeName(), "Database created successfully.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String msg = "Old Version: " + oldVersion + ", New Version: " + newVersion;
        Log.i(MeadMateData.class.getTypeName(), msg);

        switch(oldVersion)
        {
            case 1:
                db.execSQL(ADD_NEW_EVENT_TYPES);
            case 2:
                db.execSQL(CREATE_TAGS_TABLE);
                db.execSQL(CREATE_MEAD_TAGS_TABLE);
            case 3:
                db.execSQL(ALTER_MEAD_TABLE);
            case 4:
                db.execSQL(ADD_NEW_EVENT_TYPE_REL6);
            case 5:
                db.execSQL(CREATE_RECIPES_TABLE);
            case 6:
                db.execSQL(ADD_NEW_EVENT_TYPE_REL8);
            case 7:
                db.execSQL(ADD_NEW_EVENT_TYPE_REL12);
            case 8:
                db.execSQL(FIX_BAD_MEADS_DATE_DATA);
                db.execSQL(FIX_BAD_EVENTS_DATE_DATA);
                db.execSQL(FIX_BAD_READINGS_DATE_DATA);
                break;
            default:
                //log no update applied
                Log.i(MeadMateData.class.getTypeName(), "No upgrades applied. OldVersion: " + oldVersion);
        }
    }

    public static int getDbVersion() {
        return DB_VERSION;
    }

    // **** CRUD (Create, Read, Update, Delete) Operations ***** //
    int addRecipe(Recipe recipe)
    {
        try
        {
            if(recipe == null)
            {
                throw new IllegalArgumentException("Recipe object cannot be null");
            }

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_RECIPE_NAME, recipe.getName());
            values.put(KEY_RECIPE_DESC, recipe.getDescription());

            // Inserting Row
            db.insert(TABLE_RECIPES, null, values);

            // Query for new mead ID
            int recipeId = 0;
            Cursor c = db.rawQuery ("SELECT LAST_INSERT_ROWID()", null);

            if(c.getCount() > 0)
            {
                c.moveToFirst();

                recipeId = c.getInt(0);
            }

            c.close();

            return recipeId;
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());

            return 0;
        }
    }

    @SuppressLint("Range")
    List<Recipe> getRecipes()
    {
        List<Recipe> model = new ArrayList<>();

        String[] tableColumns = new String[] {
                KEY_RECIPE_ID,
                KEY_RECIPE_NAME,
                KEY_RECIPE_DESC
        };

        String orderBy = KEY_RECIPE_NAME;

        try
        {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.query(TABLE_RECIPES, tableColumns, null, null,
                    null, null, orderBy, null);

            if(c.getCount() > 0)
            {
                c.moveToFirst();

                do {

                    Recipe recipe = new Recipe();

                    recipe.setId(c.getInt(c.getColumnIndex(KEY_MEAD_ID)));
                    recipe.setName(c.getString(c.getColumnIndex(KEY_MEAD_NAME)));
                    recipe.setDescription(c.getString(c.getColumnIndex(KEY_MEAD_DESC)));

                    model.add(recipe);

                }while(c.moveToNext());
            }

            c.close();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }

        return model;
    }

    @SuppressLint("Range")
    Recipe getRecipe(int recipeId)
    {
        Recipe model = new Recipe();

        String[] tableColumns = new String[] {
                KEY_RECIPE_ID,
                KEY_RECIPE_NAME,
                KEY_RECIPE_DESC
        };

        String whereClause = KEY_RECIPE_ID + " = ?";

        String[] whereArgs = new String[] {
                String.valueOf(recipeId)
        };

        try
        {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.query(TABLE_RECIPES, tableColumns, whereClause, whereArgs,
                    null, null, null, null);

            if(c.getCount() > 0)
            {
                c.moveToFirst();

                model.setId(recipeId);
                model.setName(c.getString(c.getColumnIndex(KEY_RECIPE_NAME)));
                model.setDescription(c.getString(c.getColumnIndex(KEY_RECIPE_DESC)));
            }

            c.close();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }

        return model;
    }

    void updateRecipe(Recipe recipe)
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_RECIPE_NAME, recipe.getName());
            values.put(KEY_RECIPE_DESC, recipe.getDescription());

            String whereClause = KEY_RECIPE_ID + " = ?";

            // Update row
            db.update(TABLE_RECIPES, values, whereClause, new String[]{ String.valueOf(recipe.getId()) });
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }
    }

    void deleteRecipe(int recipeId)
    {
        String whereClause = KEY_RECIPE_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(recipeId) };

        SQLiteDatabase db = null;

        try
        {
            db = this.getWritableDatabase();

            db.beginTransaction();

            Log.i(MeadMateData.class.getTypeName(),"Deleting recipe record " + recipeId);
            db.delete(TABLE_RECIPES, whereClause, whereArgs);

            db.setTransactionSuccessful();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }
        finally
        {
            if(db != null)
            {
                db.endTransaction();
            }
        }
    }

    int addMead(Mead mead) {

        try
        {
            if(mead == null)
            {
                throw new IllegalArgumentException("Mead object cannot be null");
            }

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_MEAD_NAME, mead.getName());
            values.put(KEY_MEAD_START_DATE, mead.getStartDate());
            values.put(KEY_MEAD_DESC, mead.getDescription());
            values.put(KEY_MEAD_ORIG_GRAV, mead.getOriginalGravity());
            values.put(KEY_MEAD_ARCHIVED, (mead.getArchived() ? 1 : 0));

            // Inserting Row
            db.insert(TABLE_MEADS, null, values);

            // Query for new mead ID
            int meadId = 0;
            Cursor c = db.rawQuery ("SELECT LAST_INSERT_ROWID()", null);

            if(c.getCount() > 0)
            {
                c.moveToFirst();

                meadId = c.getInt(0);
            }

            c.close();

            return meadId;
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());

            return 0;
        }
    }

    void updateMead(Mead mead) {

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_MEAD_NAME, mead.getName());
            values.put(KEY_MEAD_START_DATE, mead.getStartDate());
            values.put(KEY_MEAD_DESC, mead.getDescription());
            values.put(KEY_MEAD_ORIG_GRAV, mead.getOriginalGravity());
            values.put(KEY_MEAD_ARCHIVED, (mead.getArchived() ? 1 : 0));

            String whereClause = KEY_MEAD_ID + " = ?";

            // Update row
            db.update(TABLE_MEADS, values, whereClause, new String[]{ String.valueOf(mead.getId()) });
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }
    }

    void addReading(Reading reading) {

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_READINGS_MEADID, reading.getMeadId());
            values.put(KEY_READINGS_DATE, reading.getDate());
            values.put(KEY_READINGS_GRAV, reading.getSpecificGravity());

            db.insert(TABLE_READINGS, null, values);
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }
    }

    void addEvent(Event event) {

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_EVENT_MEADID, event.getMeadId());
            values.put(KEY_EVENT_DATE, event.getDate());
            values.put(KEY_EVENT_TYPEID, event.getTypeId());
            values.put(KEY_EVENT_DESC, event.getDescription());

            db.insert(TABLE_EVENTS, null, values);
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }
    }

    void updateEvent(Event event)
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_EVENT_DATE, event.getDate());
            values.put(KEY_EVENT_TYPEID, event.getTypeId());
            values.put(KEY_EVENT_DESC, event.getDescription());

            String whereClause = KEY_EVENT_ID + " = ?";

            db.update(TABLE_EVENTS, values, whereClause, new String[]{ String.valueOf(event.getId()) });
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }
    }

    void archiveMead(int meadId)
    {
        setMeadArchiveFlag(meadId, true);
    }

    void unarchiveMead(int meadId)
    {
        setMeadArchiveFlag(meadId, false);
    }

    void setMeadArchiveFlag(int meadId, boolean shouldArchive)
    {
        int flag = (shouldArchive ? 1 : 0);

        setMeadArchiveFlag(meadId, flag);
    }

    void setMeadArchiveFlag(int meadId, int archiveBit)
    {
        try
        {
            if(archiveBit > 1 || archiveBit < 0)
            {
                throw new IllegalArgumentException("archiveBit value out of range");
            }

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_MEAD_ARCHIVED, archiveBit);

            String whereClause = KEY_MEAD_ID + " = ?";

            db.update(TABLE_MEADS, values, whereClause, new String[]{ String.valueOf(meadId) });
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }
    }

    void deleteMead(int meadId)
    {
        String whereClause = KEY_MEAD_ID + "=?";
        String meadTagsWhereClause = KEY_MEAD_TAGS_MEAD_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(meadId) };

        SQLiteDatabase db = null;

        try
        {
            db = this.getWritableDatabase();

            db.beginTransaction();

            Log.i(MeadMateData.class.getTypeName(),"Deleting mead record " + meadId);
            db.delete(TABLE_MEADS, whereClause, whereArgs);

            Log.i(MeadMateData.class.getTypeName(),"Deleting mead tag records for mead ID " + meadId);
            db.delete(TABLE_MEAD_TAGS, meadTagsWhereClause, whereArgs);

            db.setTransactionSuccessful();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }
        finally
        {
            if(db != null)
            {
                db.endTransaction();
            }
        }
    }

    void deleteReading(int readingId)
    {
        String whereClause = "_ID=?";
        String[] whereArgs = new String[] { String.valueOf(readingId) };

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();

            db.delete(TABLE_READINGS, whereClause, whereArgs);
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }
    }

    void deleteEvent(int eventId)
    {
        String whereClause = "_ID=?";
        String[] whereArgs = new String[] { String.valueOf(eventId) };

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();

            db.delete(TABLE_EVENTS, whereClause, whereArgs);
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }
    }

    int addTag(String name) {

        int tagId;

        try
        {
            if(name == null || name.isEmpty())
            {
                Log.e(MeadMateData.class.getTypeName(), "Parameter is null or empty.");

                return 0; // Not using variable in case it gets renamed, reassigned or moved.
            }

            SQLiteDatabase db = this.getWritableDatabase();

            String[] tableColumns = new String[] {
                    KEY_TAGS_ID
            };

            String whereClause = KEY_TAGS_NAME + " = ?";

            String[] whereArgs = new String[] {
                    name
            };

            Cursor c = db.query(TABLE_TAGS, tableColumns, whereClause, whereArgs,
                    null, null, null, null);

            if(c.getCount() > 0)
            {
                c.moveToFirst();

                tagId = c.getInt(c.getColumnIndex(KEY_TAGS_ID));
            }
            else
            {
                ContentValues values = new ContentValues();
                values.put(KEY_TAGS_NAME, name);

                // Inserting Row
                tagId = (int)db.insert(TABLE_TAGS, null, values);
            }

            c.close();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());

            return 0;  // Not using variable in case it gets renamed, reassigned or moved.
        }

        return tagId;
    }

    List<Tag> getMeadTags(int meadId)
    {
        List<Tag> model = new ArrayList<>();

        String query = "SELECT DISTINCT " + TABLE_TAGS + "." + KEY_TAGS_ID + ", " + TABLE_TAGS + "." + KEY_TAGS_NAME +
                " FROM " + TABLE_TAGS +
                " INNER JOIN " + TABLE_MEAD_TAGS +
                " ON " + TABLE_TAGS + "." + KEY_TAGS_ID + " = " + TABLE_MEAD_TAGS + "." + KEY_MEAD_TAGS_TAG_ID +
                " WHERE " + TABLE_MEAD_TAGS + "." + KEY_MEAD_TAGS_MEAD_ID + " = ? " +
                " ORDER BY " + TABLE_TAGS + "." + KEY_TAGS_NAME;

        try
        {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.rawQuery(query, new String[] { String.valueOf(meadId) });

            if(c.getCount() > 0)
            {
                c.moveToFirst();

                do {

                    Tag tag = new Tag();

                    tag.setId(c.getInt(c.getColumnIndex(KEY_TAGS_ID)));
                    tag.setName(c.getString(c.getColumnIndex(KEY_TAGS_NAME)));

                    model.add(tag);

                }while(c.moveToNext());
            }

            c.close();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }

        return model;
    }

    List<Tag> getTags()
    {
        List<Tag> model = new ArrayList<>();

        String[] tableColumns = new String[] {
                KEY_TAGS_ID,
                KEY_TAGS_NAME
        };

        //String whereClause = null;
        //String[] whereArgs = null;
        //String groupBy = null;
        //String having = null;
        //String orderBy = KEY_TAGS_NAME;
        //String limit = null;

        try
        {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.query(TABLE_TAGS, tableColumns, null, null,
                    null, null, KEY_TAGS_NAME, null);

            if(c.getCount() > 0)
            {
                c.moveToFirst();

                do {

                    Tag tag = new Tag();

                    tag.setId(c.getInt(c.getColumnIndex(KEY_TAGS_ID)));
                    tag.setName(c.getString(c.getColumnIndex(KEY_TAGS_NAME)));

                    model.add(tag);

                }while(c.moveToNext());
            }

            c.close();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }

        return model;
    }

    void addMeadTag(int meadId, int tagId)
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_MEAD_TAGS_MEAD_ID, meadId);
            values.put(KEY_MEAD_TAGS_TAG_ID, tagId);

            db.insert(TABLE_MEAD_TAGS, null, values);
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }
    }

    public void deleteMeadTag(int meadId, String tagName)
    {
        String[] tableColumns = new String[] {
                KEY_TAGS_ID
        };
        String tagsWhereClause = KEY_TAGS_NAME + "=?";
        String[] tagsWhereArgs = new String[] { tagName };

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();

            int tagId = 0;

            Cursor c = db.query(TABLE_TAGS, tableColumns, tagsWhereClause, tagsWhereArgs,
                    null, null, null, null);

            if(c.getCount() > 0)
            {
                c.moveToFirst();

                do {

                    tagId = c.getInt(c.getColumnIndex(KEY_TAGS_ID));

                }while(c.moveToNext());
            }

            String meadTagsWhereClause = KEY_MEAD_TAGS_MEAD_ID + "=? AND " + KEY_MEAD_TAGS_TAG_ID + "=?";
            String[] meadTagsWhereArgs = new String[] { String.valueOf(meadId), String.valueOf(tagId) };

            db.delete(TABLE_MEAD_TAGS, meadTagsWhereClause, meadTagsWhereArgs);

            c.close();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }
    }

    Mead getMead(int meadId)
    {
        Mead model = null;

        String[] tableColumns = new String[] {
                KEY_MEAD_ID,
                KEY_MEAD_NAME,
                KEY_MEAD_START_DATE,
                KEY_MEAD_DESC,
                KEY_MEAD_ORIG_GRAV,
                KEY_MEAD_ARCHIVED
        };

        String whereClause = KEY_MEAD_ID + " = ?";

        String[] whereArgs = new String[] {
                String.valueOf(meadId)
        };

        //String groupBy = null;
        //String having = null;
        //String orderBy = null;
        //String limit = null;

        try {

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.query(TABLE_MEADS, tableColumns, whereClause, whereArgs,
                    null, null, null, null);

            if(c.getCount() > 0)
            {
                c.moveToFirst();

                model = new Mead();

                model.setId(c.getInt(c.getColumnIndex(KEY_MEAD_ID)));
                model.setName(c.getString(c.getColumnIndex(KEY_MEAD_NAME)));
                model.setStartDate(c.getString(c.getColumnIndex(KEY_MEAD_START_DATE)));
                model.setDescription(c.getString(c.getColumnIndex(KEY_MEAD_DESC)));
                model.setOriginalGravity(c.getString(c.getColumnIndex(KEY_MEAD_ORIG_GRAV)));
                model.setArchived(c.getInt(c.getColumnIndex(KEY_MEAD_ARCHIVED)) == 1);
            }

            c.close();
        }
        catch (Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }

        return model;
    }

    List<Mead> getMeads(String orderBy, boolean includeArchived)
    {
        List<Mead> model = new ArrayList<>();

        String[] tableColumns = new String[] {
                KEY_MEAD_ID,
                KEY_MEAD_NAME,
                KEY_MEAD_START_DATE,
                KEY_MEAD_DESC,
                KEY_MEAD_ORIG_GRAV,
                KEY_MEAD_ARCHIVED
        };

        String whereClause = includeArchived ? null : KEY_MEAD_ARCHIVED + "=0";

        try
        {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.query(TABLE_MEADS, tableColumns, whereClause, null,
                    null, null, orderBy, null);

            if(c.getCount() > 0)
            {
                c.moveToFirst();

                do {

                    Mead mead = new Mead();

                    mead.setId(c.getInt(c.getColumnIndex(KEY_MEAD_ID)));
                    mead.setName(c.getString(c.getColumnIndex(KEY_MEAD_NAME)));
                    mead.setStartDate(c.getString(c.getColumnIndex(KEY_MEAD_START_DATE)));
                    mead.setDescription(c.getString(c.getColumnIndex(KEY_MEAD_DESC)));
                    mead.setOriginalGravity(c.getString(c.getColumnIndex(KEY_MEAD_ORIG_GRAV)));
                    mead.setArchived(c.getInt(c.getColumnIndex(KEY_MEAD_ARCHIVED)) == 1);

                    model.add(mead);

                }while(c.moveToNext());
            }

            c.close();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }

        return model;
    }

    List<Reading> getReadings(int meadId)
    {
        List<Reading> model = new ArrayList<>();

        String[] tableColumns = new String[] {
                KEY_READINGS_ID,
                KEY_READINGS_MEADID,
                KEY_READINGS_DATE,
                KEY_READINGS_GRAV
        };

        String whereClause = KEY_READINGS_MEADID + " = ?";

        String[] whereArgs = new String[] {
                String.valueOf(meadId)
        };

        //String groupBy = null;
        //String having = null;
        String orderBy = KEY_READINGS_DATE + ", " + KEY_READINGS_ID; // Make sure readings appear in date and input order for when two readings are taken on the same day
        //String limit = null;

        try
        {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.query(TABLE_READINGS, tableColumns, whereClause, whereArgs,
                    null, null, orderBy, null);

            if(c.getCount() > 0)
            {
                c.moveToFirst();

                do {

                    Reading reading = new Reading();

                    reading.setId(c.getInt(c.getColumnIndex(KEY_READINGS_ID)));
                    reading.setMeadId(c.getInt(c.getColumnIndex(KEY_READINGS_MEADID)));
                    reading.setDate(c.getString(c.getColumnIndex(KEY_READINGS_DATE)));
                    reading.setSpecificGravity(c.getString(c.getColumnIndex(KEY_READINGS_GRAV)));

                    model.add(reading);

                }while(c.moveToNext());
            }

            c.close();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }

        return model;
    }

    List<CombinedMeadRecord> getMeadRecords()
    {
        List<CombinedMeadRecord> model = new ArrayList<>();

        String query = "select m._ID AS MeadId, m.NAME as BatchName, m.START_DATE as 'Start Date', m.DESCRIPTION as Description, m.ORIGINAL_GRAVITY as 'Starting Gravity', m.ARCHIVED as Archived, '' as Tags, " +
            "e.DATE as 'Event Date', et.NAME as 'Event Type', e.DESCRIPTION as 'Event Description / Value' " +
            "FROM " +
            "MEADS AS m " +
            "LEFT JOIN EVENTS AS e " +
            "on e.MEAD_ID = m._ID " +
            "INNER JOIN EVENT_TYPES AS et " +
            "on e.TYPE_ID = et._ID " +
            "UNION " +
            "select m._ID AS MeadId, m.NAME as BatchName, m.START_DATE as 'Start Date', m.DESCRIPTION as Description, m.ORIGINAL_GRAVITY as 'Starting Gravity', m.ARCHIVED as Archived, '' as Tags, " +
            "r.DATE, 'Gravity Reading', r.SPECIFIC_GRAVITY " +
            "FROM " +
            "MEADS AS m " +
            "LEFT JOIN READINGS AS r " +
            "on r.MEAD_ID = m._ID " +
            "ORDER BY m._ID";

        try
        {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.rawQuery(query, null);

            if(c.getCount() > 0)
            {
                c.moveToFirst();

                do {
                    
                    List<Tag> tags = getMeadTags(c.getInt(0));
                    String tagsString = "";

                    if(tags.size() > 0)
                    {
                        for (Tag tag : tags) {
                            tagsString = tagsString.concat(tag.getName()).concat(",");
                        }

                        Log.d("MeadMateData","TagsString: " + tagsString);

                        tagsString = tagsString.substring(0, tagsString.length() - 1);
                    }

                    CombinedMeadRecord record = new CombinedMeadRecord();

                    record.setMeadId(c.getInt(0));
                    record.setBatchName(c.getString(1));
                    record.setStartDate(c.getString(2));
                    record.setDescription(c.getString(3));
                    record.setStartingGravity(c.getString(4));
                    record.setArchived((c.getInt(5) == 1) ? "true" : "false");
                    record.setTags(tagsString);
                    record.setEventDate(c.getString(7));
                    record.setEventType(c.getString(8));
                    record.setEventDescription(c.getString(9));

                    model.add(record);

                }while(c.moveToNext());
            }

            c.close();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(),ex.toString());
        }

        return model;
    }

    List<Event> getEvents(int meadId)
    {
        List<Event> model = new ArrayList<>();

        String query = "SELECT EVENTS._ID, MEAD_ID, DATE, TYPE_ID, DESCRIPTION, NAME " +
                "FROM EVENTS " +
                "INNER JOIN EVENT_TYPES " +
                "ON EVENT_TYPES._ID = EVENTS.TYPE_ID " +
                "WHERE EVENTS.MEAD_ID = ? " +
                "ORDER BY DATE, EVENTS._ID"; // Make sure events appear in date and input order for when two events are logged on the same day

        try
        {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.rawQuery(query, new String[] { String.valueOf(meadId) });

            if(c.getCount() > 0)
            {
                c.moveToFirst();

                do {

                    Event event = new Event();

                    event.setId(c.getInt(c.getColumnIndex(KEY_EVENT_ID)));
                    event.setMeadId(c.getInt(c.getColumnIndex(KEY_EVENT_MEADID)));
                    event.setDate(c.getString(c.getColumnIndex(KEY_EVENT_DATE)));
                    event.setTypeId(c.getInt(c.getColumnIndex(KEY_EVENT_TYPEID)));
                    event.setDescription(c.getString(c.getColumnIndex(KEY_EVENT_DESC)));
                    event.setTypeName(c.getString(c.getColumnIndex(KEY_EVENT_TYPE_NAME)));

                    model.add(event);

                }while(c.moveToNext());
            }

            c.close();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(),ex.toString());
        }

        return model;
    }

    Event getEvent(int eventId)
    {
        Event model = new Event();

        String[] tableColumns = new String[] {
                KEY_EVENT_MEADID,
                KEY_EVENT_DATE,
                KEY_EVENT_DESC,
                KEY_EVENT_TYPEID
        };

        String whereClause = KEY_EVENT_ID + " = ?";

        String[] whereArgs = new String[] {
                String.valueOf(eventId)
        };

        //String groupBy = null;
        //String having = null;
        //String orderBy = null;
        //String limit = null;

        try
        {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.query(TABLE_EVENTS, tableColumns, whereClause, whereArgs,
                    null, null, null, null);

            if(c.getCount() > 0)
            {
                c.moveToFirst();

                model.setId(eventId);
                model.setTypeId(c.getInt(c.getColumnIndex(KEY_EVENT_TYPEID)));
                model.setMeadId(c.getInt((c.getColumnIndex(KEY_EVENT_MEADID))));
                model.setDescription(c.getString(c.getColumnIndex(KEY_EVENT_DESC)));
                model.setDate(c.getString(c.getColumnIndex(KEY_EVENT_DATE)));
            }

            c.close();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }

        return model;
    }

    String getEventDescription(int id)
    {
        String eventDescription = "";

        String[] tableColumns = new String[] {
                KEY_EVENT_DESC
        };

        String whereClause = KEY_EVENT_ID + " = ?";

        String[] whereArgs = new String[] {
                String.valueOf(id)
        };

        //String groupBy = null;
        //String having = null;
        //String orderBy = null;
        //String limit = null;

        try
        {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.query(TABLE_EVENTS, tableColumns, whereClause, whereArgs,
                    null, null, null, null);

            if(c.getCount() > 0)
            {
                c.moveToFirst();

                eventDescription = c.getString(c.getColumnIndex(KEY_EVENT_DESC));
            }

            c.close();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }

        return eventDescription;
    }

    List<EventType> getEventTypes()
    {
        List<EventType> model = new ArrayList<>();

        String[] tableColumns = new String[] {
                KEY_EVENT_TYPE_ID, KEY_EVENT_TYPE_NAME
        };

        //String whereClause = null;
        //String groupBy = null;
        //String having = null;
        //String orderBy = null;
        //String limit = null;

        try
        {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.query(TABLE_EVENT_TYPES, tableColumns, null, null,
                    null, null, KEY_EVENT_TYPE_NAME, null);

            if(c.getCount() > 0)
            {
                c.moveToFirst();

                do {

                    EventType eventType = new EventType();

                    eventType.setId(c.getInt(c.getColumnIndex(KEY_EVENT_TYPE_ID)));
                    eventType.setName(c.getString(c.getColumnIndex(KEY_EVENT_TYPE_NAME)));

                    model.add(eventType);

                }while(c.moveToNext());
            }

            c.close();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(),ex.toString());
        }

        return model;
    }

    public void splitMead(int meadId, int count, boolean canBeDeleted)
    {
        Log.d(MeadMateData.class.getTypeName(), "Mead ID: " + meadId);
        Log.d(MeadMateData.class.getTypeName(), "Count: " + count);
        Log.d(MeadMateData.class.getTypeName(), "CanBeDeleted: " + canBeDeleted);

        Mead mead;
        List<Event> meadEvents;
        List<Reading> meadReadings;
        List<Tag> meadTags;

        SQLiteDatabase db = null;

        try
        {
            db = this.getWritableDatabase();

            db.beginTransaction();

            mead = getMead(meadId);

            if(mead == null)
            {
                throw new NullPointerException("Mead record is null. SplitMead cannot continue.");
            }

            // Gather related events
            meadEvents = getEvents(meadId);
            meadReadings = getReadings(meadId);
            meadTags = getMeadTags(meadId);

            // Create x mead records
            for (int i = 0; i < count; i++)
            {
                String suffix = " #" + (i+1);
                
                Mead clone = new Mead();
                clone.setName(mead.getName() + suffix);
                clone.setDescription(mead.getDescription());
                clone.setOriginalGravity(mead.getOriginalGravity());
                clone.setStartDate(mead.getStartDate());
                clone.setArchived(false); // Doesn't make sense to archive a fresh split
                
                int cloneId = addMead(clone);

                for (Event event : meadEvents)
                {
                    // override event's mead ID and save record
                    event.setMeadId(cloneId);
                    addEvent(event);
                }

                for (Reading reading : meadReadings)
                {
                    // override reading's mead ID and save record
                    reading.setMeadId(cloneId);
                    addReading(reading);
                }

                for (Tag tag : meadTags)
                {
                    addMeadTag(cloneId, tag.getId());
                }
            }

            if(canBeDeleted)
            {
                deleteMead(meadId);
            }

            // Commit transaction
            db.setTransactionSuccessful();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }
        finally
        {
            if(db != null)
            {
                db.endTransaction();
            }
        }
    }
}
