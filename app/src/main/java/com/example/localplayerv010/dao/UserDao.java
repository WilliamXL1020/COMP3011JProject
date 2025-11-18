package com.example.localplayerv010.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.localplayerv010.model.User;

import java.util.List;

@Dao
public interface UserDao {

    // 插入新用户
    @Insert
    long insertUser(User user);

    // 根据用户名查找用户
    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);

    // 根据邮箱查找用户
    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);

    // 验证登录
    @Query("SELECT * FROM users WHERE (username = :usernameOrEmail OR email = :usernameOrEmail) AND password = :password")
    User login(String usernameOrEmail, String password);

    // 更新最后登录时间
    @Query("UPDATE users SET lastLoginTime = :loginTime WHERE id = :userId")
    void updateLoginTime(int userId, long loginTime);

    // 检查用户名是否存在
    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    int checkUsernameExists(String username);

    // 检查邮箱是否存在
    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int checkEmailExists(String email);

    // 获取用户数量（用于调试）
    @Query("SELECT COUNT(*) FROM users")
    int getUserCount();

    // 获取所有用户（用于调试）
    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    // 更新用户信息
    @Update
    int updateUser(User user);
}