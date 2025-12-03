package com.example.localplayerv010.database;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.localplayerv010.dao.BrowseHistoryDao;
import com.example.localplayerv010.dao.UserDao;
import com.example.localplayerv010.model.BrowseHistory;
import com.example.localplayerv010.model.User;

import java.io.File;

@Database(entities = {User.class,BrowseHistory.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract UserDao userDao();
    public abstract BrowseHistoryDao browseHistoryDao();


    // Singleton pattern
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    // First, repair the empty users table.
                    fixEmptyUsersTable(context);

                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "local_player_db"
                    )
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                    Log.d("Database", "✅ Database opened successfully, table structure is correct");
                                }

                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Log.d("Database", "✅ New database created successfully.");
                                }
                            })
                            .build();
                }
            }
        }
        return instance;
    }

    private static void fixEmptyUsersTable(Context context) {
        try {
            File dbFile = context.getDatabasePath("local_player_db");
            Log.d("Fix", "Check database files: " + dbFile.exists());

            if (dbFile.exists()) {
                SQLiteDatabase db = SQLiteDatabase.openDatabase(
                        dbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE
                );

                // Check if the users table has a column.
                Cursor cursor = db.rawQuery("PRAGMA table_info(users)", null);
                int columnCount = cursor != null ? cursor.getCount() : 0;
                Log.d("Fix", "Number of columns in users table: " + columnCount);

                if (cursor != null) {
                    cursor.close();
                }

                if (columnCount == 0) {
                    Log.d("Fix", "🔧 Repair the empty users table...");
                    // Delete the empty users table
                    db.execSQL("DROP TABLE IF EXISTS users");
                    // Create the correct users table
                    db.execSQL("CREATE TABLE users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "username TEXT, " +
                            "email TEXT, " +
                            "password TEXT, " +
                            "avatarUrl TEXT, " +
                            "createTime INTEGER NOT NULL, " +
                            "lastLoginTime INTEGER NOT NULL)");
                    Log.d("Fix", "✅ The empty users table has been repaired.");
                } else {
                    Log.d("Fix", "✅ The users table structure is normal.");
                }

                db.close();
            } else {
                Log.d("Fix", "数据库文件不存在，将创建新数据库");
            }
        } catch (Exception e) {
            Log.e("Fix", "修复失败: " + e.getMessage());
        }
    }






}
