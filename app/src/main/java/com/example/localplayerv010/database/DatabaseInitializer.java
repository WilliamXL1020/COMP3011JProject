package com.example.localplayerv010.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

public class DatabaseInitializer extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseInitializer";
    private static final String DATABASE_NAME = "local_player_db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseInitializer(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // This method is usually not called because the database already exists.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Database upgrade logic
    }

    public static void initializeTables(AppDatabase database) {
        // Execute in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                SupportSQLiteDatabase db = database.getOpenHelper().getWritableDatabase();

                // Check if the users table exists
                Cursor cursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='users'");
                boolean usersTableExists = cursor.getCount() > 0;
                cursor.close();

                if (!usersTableExists) {
                    // Manually create the users table
                    db.execSQL("CREATE TABLE IF NOT EXISTS `users` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`username` TEXT, " +
                            "`email` TEXT, " +
                            "`password` TEXT, " +
                            "`avatarUrl` TEXT, " +
                            "`createTime` INTEGER NOT NULL, " +
                            "`lastLoginTime` INTEGER NOT NULL)");
                    Log.d("Database", "✅ The Users table was created successfully.");
                }

                // Check the browse_history table
                cursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='browse_history'");
                boolean historyTableExists = cursor.getCount() > 0;
                cursor.close();

                if (!historyTableExists) {
                    // Manually create the browse_history table
                    db.execSQL("CREATE TABLE IF NOT EXISTS `browse_history` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`userId` INTEGER NOT NULL, " +
                            "`videoId` TEXT NOT NULL, " +
                            "`videoTitle` TEXT, " +
                            "`category` TEXT, " +
                            "`watchDuration` INTEGER NOT NULL, " +
                            "`watchTime` INTEGER, " +
                            "`lastPosition` INTEGER NOT NULL)");
                    Log.d("Database", "✅ The BrowseHistory table was created successfully");
                }

            } catch (Exception e) {
                Log.e("Database", "❌ Table initialization failed: " + e.getMessage());
            }
        });
    }


    private boolean isTableExists(SQLiteDatabase db, String tableName) {
        try {
            db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null).close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
