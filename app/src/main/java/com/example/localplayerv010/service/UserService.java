package com.example.localplayerv010.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.localplayerv010.dao.UserDao;
import com.example.localplayerv010.database.AppDatabase;
import com.example.localplayerv010.model.User;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class UserService {
    private UserDao userDao;
    private Executor executor;

    public UserService(Context context){
        AppDatabase database = AppDatabase.getInstance(context);
        this.userDao = database.userDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void register(User user, RegisterCallback callback) {
        executor.execute(()->{
            try {
                if (userDao.checkUsernameExists(user.getUsername())>0){
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure("Username already exists"));
                    return;
                }

                if (userDao.checkEmailExists(user.getEmail())>0){
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure("The email address already exists"));
                    return;
                }

                long userId = userDao.insertUser(user);
                Log.d("UserService", "✅ User registration successful, ID: " + userId);

                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onSuccess(userId));



            }catch (Exception e) {
                Log.e("UserService", "❌ Registration failed: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onFailure("failed to register: " + e.getMessage()));
            }
        });
    }


    public void login(String usernameOrEmail, String password, LoginCallback callback) {
        executor.execute(() -> {
            try {
                Log.d("UserService", "Start logging: " + usernameOrEmail);

                User user = userDao.login(usernameOrEmail, password);
                if (user != null) {
                    // 更新登录时间
                    userDao.updateLoginTime(user.getId(), System.currentTimeMillis());
                    Log.d("UserService", "✅ Login successful, User ID: " + user.getId());

                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onSuccess(user));
                } else {
                    Log.d("UserService", "❌ Login failed: Incorrect username or password");
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure("Username or password incorrect"));
                }
            } catch (Exception e) {
                Log.e("UserService", "❌ Login error: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onFailure("Login failed: " + e.getMessage()));
            }
        });
    }


    public interface RegisterCallback {
        void onSuccess(long userId);
        void onFailure(String errorMessage);
    }
    public interface LoginCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }
    public interface CheckCallback {
        void onResult(boolean exists);
    }
}
