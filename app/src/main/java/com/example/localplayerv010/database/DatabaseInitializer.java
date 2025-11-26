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
        // 这个方法通常不会被调用，因为数据库已经存在
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 数据库升级逻辑
    }

    public static void initializeTables(AppDatabase database) {
        // 在后台线程执行
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                SupportSQLiteDatabase db = database.getOpenHelper().getWritableDatabase();

                // 检查users表是否存在
                Cursor cursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='users'");
                boolean usersTableExists = cursor.getCount() > 0;
                cursor.close();

                if (!usersTableExists) {
                    // 手动创建users表
                    db.execSQL("CREATE TABLE IF NOT EXISTS `users` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`username` TEXT, " +
                            "`email` TEXT, " +
                            "`password` TEXT, " +
                            "`avatarUrl` TEXT, " +
                            "`createTime` INTEGER NOT NULL, " +
                            "`lastLoginTime` INTEGER NOT NULL)");
                    Log.d("Database", "✅ Users表创建成功");
                }

                // 检查browse_history表
                cursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='browse_history'");
                boolean historyTableExists = cursor.getCount() > 0;
                cursor.close();

                if (!historyTableExists) {
                    // 手动创建browse_history表
                    db.execSQL("CREATE TABLE IF NOT EXISTS `browse_history` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`userId` INTEGER NOT NULL, " +
                            "`videoId` TEXT NOT NULL, " +
                            "`videoTitle` TEXT, " +
                            "`category` TEXT, " +
                            "`watchDuration` INTEGER NOT NULL, " +
                            "`watchTime` INTEGER, " +
                            "`lastPosition` INTEGER NOT NULL)");
                    Log.d("Database", "✅ BrowseHistory表创建成功");
                }

            } catch (Exception e) {
                Log.e("Database", "❌ 初始化表失败: " + e.getMessage());
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
