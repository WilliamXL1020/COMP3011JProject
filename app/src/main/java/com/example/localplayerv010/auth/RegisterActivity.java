package com.example.localplayerv010.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localplayerv010.R;
import com.example.localplayerv010.model.User;
import com.example.localplayerv010.service.UserService;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginLink;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        userService = new UserService(this);

        setupClickListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLoginLink.setOnClickListener(v -> navigateToLogin());
    }

    private void attemptRegister() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (validateInputs(username, email, password, confirmPassword)) {
            registerUser(username, email, password);
        }
    }

    private boolean validateInputs(String username, String email, String password, String confirmPassword) {
        if (username.isEmpty()) {
            etUsername.setError("Please enter your username");
            return false;
        }
        if (email.isEmpty()) {
            etEmail.setError("Please enter your email address");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Please enter your password");
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters long");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Password mismatch");
            return false;
        }
        return true;
    }

    private void registerUser(String username, String email, String password) {
        btnRegister.setEnabled(false);
        btnRegister.setText("注册中...");

        User user = new User(username, email, password);

        userService.register(user, new UserService.RegisterCallback() {
            @Override
            public void onSuccess(long userId) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                    // 注册成功后跳转到登录页面
                    navigateToLogin();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("注册");
                    Toast.makeText(RegisterActivity.this, "注册失败: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
