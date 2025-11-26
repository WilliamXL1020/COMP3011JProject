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


    // 单例模式
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    // 先修复空的users表
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
                                    Log.d("Database", "✅ 数据库打开成功，表结构正确");
                                }

                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Log.d("Database", "✅ 新数据库创建成功");
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
            Log.d("Fix", "检查数据库文件: " + dbFile.exists());

            if (dbFile.exists()) {
                SQLiteDatabase db = SQLiteDatabase.openDatabase(
                        dbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE
                );

                // 检查users表是否有列
                Cursor cursor = db.rawQuery("PRAGMA table_info(users)", null);
                int columnCount = cursor != null ? cursor.getCount() : 0;
                Log.d("Fix", "users表列数: " + columnCount);

                if (cursor != null) {
                    cursor.close();
                }

                if (columnCount == 0) {
                    Log.d("Fix", "🔧 修复空的users表...");
                    // 删除空的users表
                    db.execSQL("DROP TABLE IF EXISTS users");
                    // 创建正确的users表
                    db.execSQL("CREATE TABLE users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "username TEXT, " +
                            "email TEXT, " +
                            "password TEXT, " +
                            "avatarUrl TEXT, " +
                            "createTime INTEGER NOT NULL, " +
                            "lastLoginTime INTEGER NOT NULL)");
                    Log.d("Fix", "✅ 空的users表修复完成");
                } else {
                    Log.d("Fix", "✅ users表结构正常");
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
