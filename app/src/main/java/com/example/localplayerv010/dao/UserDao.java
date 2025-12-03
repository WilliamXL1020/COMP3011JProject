package com.example.localplayerv010.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.localplayerv010.model.User;

import java.util.List;

@Dao
public interface UserDao {

    // Insert new user
    @Insert
    long insertUser(User user);

    // Search for users by username
    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);

    // Find users by email
    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);

    // Login verification
    @Query("SELECT * FROM users WHERE (username = :usernameOrEmail OR email = :usernameOrEmail) AND password = :password")
    User login(String usernameOrEmail, String password);

    // Update last login time
    @Query("UPDATE users SET lastLoginTime = :loginTime WHERE id = :userId")
    void updateLoginTime(int userId, long loginTime);

    // Check if the username exists
    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    int checkUsernameExists(String username);

    // Check if the email address exists.
    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int checkEmailExists(String email);

    // Get the number of users (for debugging purposes)
    @Query("SELECT COUNT(*) FROM users")
    int getUserCount();

    // Get all users (for debugging)
    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    // Update user information
    @Update
    int updateUser(User user);
}