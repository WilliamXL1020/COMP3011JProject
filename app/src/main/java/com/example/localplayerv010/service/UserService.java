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
                            callback.onFailure("用户名已存在"));
                    return;
                }

                if (userDao.checkEmailExists(user.getEmail())>0){
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure("邮箱已存在"));
                    return;
                }

                long userId = userDao.insertUser(user);
                Log.d("UserService", "✅ 用户注册成功, ID: " + userId);

                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onSuccess(userId));



            }catch (Exception e) {
                Log.e("UserService", "❌ 注册失败: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onFailure("注册失败: " + e.getMessage()));
            }
        });
    }


    public void login(String usernameOrEmail, String password, LoginCallback callback) {
        executor.execute(() -> {
            try {
                Log.d("UserService", "开始登录: " + usernameOrEmail);

                User user = userDao.login(usernameOrEmail, password);
                if (user != null) {
                    // 更新登录时间
                    userDao.updateLoginTime(user.getId(), System.currentTimeMillis());
                    Log.d("UserService", "✅ 登录成功, 用户ID: " + user.getId());

                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onSuccess(user));
                } else {
                    Log.d("UserService", "❌ 登录失败: 用户名或密码错误");
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure("用户名或密码错误"));
                }
            } catch (Exception e) {
                Log.e("UserService", "❌ 登录异常: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onFailure("登录失败: " + e.getMessage()));
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
