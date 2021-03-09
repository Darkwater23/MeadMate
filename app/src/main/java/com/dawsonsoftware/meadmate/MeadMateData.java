package com.dawsonsoftware.meadmate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.RequiresPermission;

import com.dawsonsoftware.meadmate.models.Mead;
import com.dawsonsoftware.meadmate.models.Reading;

import java.util.ArrayList;
import java.util.List;

public class MeadMateData extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "appdata";

    // Meads table fields
    private static final String TABLE_MEADS = "MEADS";
    private static final String KEY_MEAD_ID = "_ID";
    private static final String KEY_MEAD_NAME = "NAME";
    private static final String KEY_MEAD_START_DATE = "START_DATE";
    private static final String KEY_MEAD_DESC = "DESCRIPTION";
    private static final String KEY_MEAD_ORIG_GRAV = "ORIGINAL_GRAVITY";

    // Mead Log table fields
    private static final String TABLE_MEAD_LOG = "MEAD_LOG";
    private static final String KEY_MEAD_LOG_ID = "_ID";
    private static final String KEY_MEAD_LOG_DATE = "DATE";
    private static final String KEY_MEAD_LOG_ENTRY = "ENTRY";

    // Readings table fields
    private static final String TABLE_READINGS = "READINGS";
    private static final String KEY_READINGS_ID = "_ID";
    private static final String KEY_READINGS_BREWID = "BREW_ID";
    private static final String KEY_READINGS_DATE = "DATE";
    private static final String KEY_READINGS_GRAV = "SPECIFIC_GRAVITY";

    public MeadMateData(Context context){
        super(context,DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MEAD_TABLE = "CREATE TABLE " + TABLE_MEADS + " (" +
                KEY_MEAD_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                KEY_MEAD_NAME + " TEXT NOT NULL," +
                KEY_MEAD_START_DATE + " TEXT NOT NULL," +
                KEY_MEAD_DESC + " TEXT," +
                KEY_MEAD_ORIG_GRAV + " TEXT NOT NULL DEFAULT 0.0)";

        String CREATE_MEAD_LOG_TABLE = "CREATE TABLE " + TABLE_MEAD_LOG + " (" +
                KEY_MEAD_LOG_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                KEY_MEAD_LOG_DATE + " TEXT NOT NULL, " +
                KEY_MEAD_LOG_ENTRY + " TEXT NOT NULL)";

        String CREATE_READINGS_TABLE = "CREATE TABLE " + TABLE_READINGS + " (" +
                KEY_READINGS_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                KEY_READINGS_BREWID + " INTEGER NOT NULL," +
                KEY_READINGS_DATE + " TEXT NOT NULL," +
                KEY_READINGS_GRAV + " TEXT NOT NULL)";

        db.execSQL(CREATE_MEAD_LOG_TABLE);
        db.execSQL(CREATE_MEAD_TABLE);
        db.execSQL(CREATE_READINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEADS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEAD_LOG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_READINGS);

        onCreate(db);
    }

    // **** CRUD (Create, Read, Update, Delete) Operations ***** //
    void addMead(Mead mead) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MEAD_NAME, mead.getName());
        values.put(KEY_MEAD_START_DATE, mead.getStartDate());
        values.put(KEY_MEAD_DESC, mead.getDescription());
        values.put(KEY_MEAD_ORIG_GRAV, mead.getOriginalGravity());

        // Inserting Row
        db.insert(TABLE_MEADS, null, values);

        db.close(); // Closing database connection
    }

    void addReading(Reading reading) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_READINGS_BREWID, reading.getBrewId());
        values.put(KEY_READINGS_DATE, reading.getDate());
        values.put(KEY_READINGS_GRAV, reading.getSpecificGravity());

        // Inserting Row
        db.insert(TABLE_READINGS, null, values);

        db.close(); // Closing database connection
    }

    void deleteMead(Mead mead)
    {
        deleteMead(mead.getId());
    }

    void deleteMead(int meadId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = "_ID=?";
        String[] whereArgs = new String[] { String.valueOf(meadId) };

        db.delete(TABLE_MEADS, whereClause, whereArgs);

        db.close();
    }

    void deleteReading(int readingId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = "_ID=?";
        String[] whereArgs = new String[] { String.valueOf(readingId) };

        db.delete(TABLE_READINGS, whereClause, whereArgs);

        db.close();
    }

    Mead getMead(int meadId)
    {
        Mead model = null;

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            String[] tableColumns = new String[] {
                    KEY_MEAD_ID,
                    KEY_MEAD_NAME,
                    KEY_MEAD_START_DATE,
                    KEY_MEAD_DESC,
                    KEY_MEAD_ORIG_GRAV
            };

            String whereClause = KEY_MEAD_ID + " = ?";

            String[] whereArgs = new String[] {
                    String.valueOf(meadId)
            };

            //String groupBy = null;
            //String having = null;
            //String orderBy = null;
            //String limit = null;

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
            }

            c.close();

            db.close();
        }
        catch (Exception ex)
        {
            model = new Mead();

            model.setId(0);
            model.setName("Error");
            model.setStartDate("01/01/2021");
            model.setOriginalGravity("1.000");
            model.setDescription(ex.toString());
        }

        return model;
    }

    List<Mead> getMeads()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Mead> model = new ArrayList<Mead>();

        String[] tableColumns = new String[] {
                KEY_MEAD_ID,
                KEY_MEAD_NAME,
                KEY_MEAD_START_DATE,
                KEY_MEAD_DESC,
                KEY_MEAD_ORIG_GRAV
        };

        //String whereClause = null;
        //String[] whereArgs = null;
        //String groupBy = null;
        //String having = null;
        //String orderBy = null;
        //String limit = null;

        Cursor c = db.query(TABLE_MEADS, tableColumns, null, null,
                null, null, null, null);

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

                model.add(mead);

            }while(c.moveToNext());
        }

        db.close();

        return model;
    }

    List<Reading> getReadings(Integer meadId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Reading> model = new ArrayList<Reading>();

        String[] tableColumns = new String[] {
                KEY_READINGS_ID,
                KEY_READINGS_BREWID,
                KEY_READINGS_DATE,
                KEY_READINGS_GRAV
        };

        String whereClause = KEY_READINGS_BREWID + " = ?";

        String[] whereArgs = new String[] {
                String.valueOf(meadId)
        };
        //String groupBy = null;
        //String having = null;
        String orderBy = KEY_READINGS_DATE;
        //String limit = null;

        Cursor c = db.query(TABLE_READINGS, tableColumns, whereClause, whereArgs,
                null, null, orderBy, null);

        if(c.getCount() > 0)
        {
            c.moveToFirst();

            do {

                Reading reading = new Reading();

                reading.setId(c.getInt(c.getColumnIndex(KEY_READINGS_ID)));
                reading.setBrewId(c.getInt(c.getColumnIndex(KEY_READINGS_BREWID)));
                reading.setDate(c.getString(c.getColumnIndex(KEY_READINGS_DATE)));
                reading.setSpecificGravity(c.getString(c.getColumnIndex(KEY_READINGS_GRAV)));

                model.add(reading);

            }while(c.moveToNext());
        }

        db.close();

        return model;
    }
}
