package com.example.grade_management;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailRegister, pswdRegister, confirmPswdRegister;
    private RadioGroup roleGroup;
    private Button btnRegister;
    private TextView tvLogin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get UI Elements
        emailRegister = findViewById(R.id.emailInputReg);
        pswdRegister = findViewById(R.id.pswdInputReg);
        confirmPswdRegister = findViewById(R.id.cnfrmPswdInputReg);
        roleGroup = findViewById(R.id.roleGroup);
        btnRegister = findViewById(R.id.btnReg);
        tvLogin = findViewById(R.id.tvRegisterReg);

        // Register Button Click Listener
        btnRegister.setOnClickListener(v -> registerUser());

        // Redirect to Login Page
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String email = emailRegister.getText().toString().trim();
        String password = pswdRegister.getText().toString().trim();
        String confirmPassword = confirmPswdRegister.getText().toString().trim();

        int selectedRoleId = roleGroup.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedRoleButton = findViewById(selectedRoleId);
        String role = selectedRoleButton.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailRegister.setError("Email is required");
            emailRegister.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            pswdRegister.setError("Password is required");
            pswdRegister.requestFocus();
            return;
        }
        if (password.length() < 6) {
            pswdRegister.setError("Password must be at least 6 characters");
            pswdRegister.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPswdRegister.setError("Passwords do not match");
            confirmPswdRegister.requestFocus();
            return;
        }

        // Firebase User Registration
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user.getUid(), email, role);
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String uid, String email, String role) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("role", role);
        userData.put("uid", uid);

        db.collection("users").document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Log.w("RegisterActivity", "Error adding user", e));
    }
}
