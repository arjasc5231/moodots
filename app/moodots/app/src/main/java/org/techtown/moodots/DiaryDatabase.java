package org.techtown.moodots;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DiaryDatabase{
    private static final String TAG = "DiaryDatabase";
    private static DiaryDatabase database;
    public static String TABLE_DIARY = "DIARY";
    public static int DATABASE_VERSION = 1;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;
    private DiaryDatabase(Context context) {
        this.context = context;
    }
    public static DiaryDatabase getInstance(Context context) {
        if (database == null) {
            Log.d(TAG, "database is null in recent");
            database = new DiaryDatabase(context);
        }
        return database;
    }

    public boolean open() {
        println("opening database [" + AppConstants.DATABASE_NAME + "].");
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return true;
    }

    public void close() {
        println("closing database [" + AppConstants.DATABASE_NAME + "].");
        db.close();
        database = null;
    }

    public Cursor rawQuery(String SQL) {
        println("\nexecuteQuery called.\n");

        Cursor c1 = null;
        try {
            c1 = db.rawQuery(SQL, null);
            println("cursor count : " + c1.getCount());
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
        }

        return c1;
    }

    public boolean execSQL(String SQL) {
        println("\nexecute called.\n");

        try {
            Log.d(TAG, "SQL : " + SQL);
            db.execSQL(SQL);
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
            return false;
        }

        return true;
    }



    /**
     * Database Helper inner class
     */
    public class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, AppConstants.DATABASE_NAME, null, DATABASE_VERSION);
            Log.d(TAG, "databasehelper");
        }

        public void onCreate(SQLiteDatabase db) {
            println("creating database [" + AppConstants.DATABASE_NAME + "].");

            // TABLE_NOTE
            println("creating table [" + TABLE_DIARY + "].");

            // drop existing table
            String DROP_SQL = "drop table if exists " + TABLE_DIARY;
            try {
                db.execSQL(DROP_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in DROP_SQL", ex);
            }

            // create table
            String CREATE_SQL = "CREATE TABLE " + TABLE_DIARY +
                    "( _id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + "MOOD INTEGER NOT NULL, "
                    + "CONTENTS TEXT NOT NULL, "
                    + "DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP );";
            try {
                db.execSQL(CREATE_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in CREATE_SQL", ex);
            }

            // create index
            String CREATE_INDEX_SQL = "create index " + TABLE_DIARY + "_IDX ON " + TABLE_DIARY + "("
                    + "CREATE_DATE"
                    + ")";
            try {
                db.execSQL(CREATE_INDEX_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in CREATE_INDEX_SQL", ex);
            }
        }

        public void onOpen(SQLiteDatabase db) {
            println("opened database [" + AppConstants.DATABASE_NAME + "].");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            println("Upgrading database from version " + oldVersion + " to " + newVersion + ".");
        }
    }
    public void println(String data) {
        Log.d(TAG, data);
    }
}
