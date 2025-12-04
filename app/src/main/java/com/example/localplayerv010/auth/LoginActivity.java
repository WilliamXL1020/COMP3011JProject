package com.example.localplayerv010.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localplayerv010.Homepage.HomeActivity;
import com.example.localplayerv010.R;
import com.example.localplayerv010.service.UserService;
import com.example.localplayerv010.service.UserPrefs;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsernameOrEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        userService = new UserService(this);

        setupClickListeners();
        checkAutoLogin();
    }

    private void initViews() {
        etUsernameOrEmail = findViewById(R.id.et_username_or_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegisterLink = findViewById(R.id.tv_register_link);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        tvRegisterLink.setOnClickListener(v -> navigateToRegister());
    }

    private void attemptLogin() {
        String usernameOrEmail = etUsernameOrEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (validateInputs(usernameOrEmail, password)) {
            loginUser(usernameOrEmail, password);
        }
    }

    private boolean validateInputs(String usernameOrEmail, String password) {
        if (usernameOrEmail.isEmpty()) {
            etUsernameOrEmail.setError("please enter username or email");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("please enter your password");
            return false;
        }
        return true;
    }

    private void loginUser(String usernameOrEmail, String password) {
        btnLogin.setEnabled(false);
        btnLogin.setText("logging...");

        userService.login(usernameOrEmail, password, new UserService.LoginCallback() {
            @Override
            public void onSuccess(com.example.localplayerv010.model.User user) {
                runOnUiThread(() -> {
                    // Save login status
                    UserPrefs.saveUserInfo(LoginActivity.this, user.getId(), user.getUsername(), user.getEmail());
                    Toast.makeText(LoginActivity.this, "successfully logged in！", Toast.LENGTH_SHORT).show();
                    navigateToHome();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("login");
                    Toast.makeText(LoginActivity.this, "login failed: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void checkAutoLogin() {
        if (UserPrefs.isLoggedIn(this)) {
            navigateToHome();
        }
    }

    private void navigateToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void navigateToRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }
}
