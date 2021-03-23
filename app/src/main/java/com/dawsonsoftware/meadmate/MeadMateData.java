package com.dawsonsoftware.meadmate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dawsonsoftware.meadmate.models.Event;
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
                "(1,\"Primary Fermentation\")," +
                "(2,\"Secondary Fermentation\")," +
                "(3,\"Racked\")," +
                "(4,\"Bottled\")," +
                "(5,\"Discarded\")," +
                "(6,\"Tasting\")";

        String CREATE_READINGS_TABLE = "CREATE TABLE " + TABLE_READINGS + " (" +
                KEY_READINGS_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                KEY_READINGS_MEADID + " INTEGER NOT NULL," +
                KEY_READINGS_DATE + " TEXT NOT NULL," +
                KEY_READINGS_GRAV + " TEXT NOT NULL)";

        db.execSQL(CREATE_MEAD_TABLE);
        db.execSQL(CREATE_EVENTS_TABLE);
        db.execSQL(CREATE_EVENT_TYPES_TABLE);
        db.execSQL(LOAD_EVENT_TYPES_TABLE);
        db.execSQL(CREATE_READINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEADS);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEAD_LOG);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_READINGS);

        onCreate(db);
    }

    // **** CRUD (Create, Read, Update, Delete) Operations ***** //
    Integer addMead(Mead mead) {

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_MEAD_NAME, mead.getName());
            values.put(KEY_MEAD_START_DATE, mead.getStartDate());
            values.put(KEY_MEAD_DESC, mead.getDescription());
            values.put(KEY_MEAD_ORIG_GRAV, mead.getOriginalGravity());

            // Inserting Row
            db.insert(TABLE_MEADS, null, values);

            // Query for new mead ID
            Integer meadId = 0;
            Cursor c = db.rawQuery ("SELECT LAST_INSERT_ROWID()", null);

            if(c.getCount() > 0)
            {
                c.moveToFirst();

                meadId = c.getInt(0);
            }

            db.close(); // Closing database connection

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

            String whereClause = KEY_MEAD_ID + " = ?";

            // Update row
            db.update(TABLE_MEADS, values, whereClause, new String[]{ mead.getId().toString() });

            db.close(); // Closing database connection
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

            // Inserting Row
            db.insert(TABLE_READINGS, null, values);

            db.close(); // Closing database connection
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

            // Inserting Row
            db.insert(TABLE_EVENTS, null, values);

            db.close(); // Closing database connection
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

            // Update row
            db.update(TABLE_EVENTS, values, whereClause, new String[]{ event.getId().toString() });

            db.close(); // Closing database connection
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }
    }

    void deleteMead(Mead mead)
    {
        deleteMead(mead.getId());
    }

    void deleteMead(int meadId)
    {
        String whereClause = "_ID=?";
        String[] whereArgs = new String[] { String.valueOf(meadId) };

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();

            db.delete(TABLE_MEADS, whereClause, whereArgs);

            db.close();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
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

            db.close();
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

            db.close();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());

            //TODO: custom exception class?
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
            }

            c.close();

            db.close();
        }
        catch (Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());

            //TODO: custom exception class?
        }

        return model;
    }

    List<Mead> getMeads()
    {
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

        try
        {
            SQLiteDatabase db = this.getReadableDatabase();

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
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());
        }

        return model;
    }

    List<Reading> getReadings(Integer meadId)
    {
        List<Reading> model = new ArrayList<Reading>();

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
        String orderBy = KEY_READINGS_DATE;
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

            db.close();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());

            //TODO: custom exception class?
        }

        return model;
    }

    List<Event> getEvents(Integer meadId)
    {
        List<Event> model = new ArrayList<Event>();

        String query = "SELECT EVENTS._ID, MEAD_ID, DATE, TYPE_ID, DESCRIPTION, NAME " +
                "FROM EVENTS " +
                "INNER JOIN EVENT_TYPES " +
                "ON EVENT_TYPES._ID = EVENTS.TYPE_ID " +
                "WHERE EVENTS.MEAD_ID = ?";

        try
        {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.rawQuery(query, new String[] { meadId.toString() });

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

            db.close();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(),ex.toString());

            //TODO: custom exception class?
        }

        return model;
    }

    Event getEvent(Integer eventId)
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

            db.close();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());

            //TODO: Add custom exception class?
        }

        return model;
    }

    String getEventDescription(Integer id)
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

            db.close();
        }
        catch(Exception ex)
        {
            Log.e(MeadMateData.class.getTypeName(), ex.toString());

            //TODO: Add custom exception class?
        }

        return eventDescription;
    }
}
