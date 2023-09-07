package com.example.notespro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notespro.databinding.ActivityLoginBinding;
import com.example.notespro.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    ActivityLoginBinding binding;
    EditText etEmail, etPassword;
    Button loginBtn;
    ProgressBar progressBar;
    TextView gotoCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth = FirebaseAuth.getInstance();

        etEmail = binding.etEmailIn;
        etPassword = binding.etPasswordIn;
        progressBar = binding.progressBar;
        loginBtn = binding.Login;
        gotoCreateAccount = binding.createAccount;

        loginBtn.setOnClickListener((v) -> loginUser());

        gotoCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, signUpActivity.class);
            startActivity(intent);
            finish();
        });

    }

    void loginUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        boolean isValidated = validateData(email, password);
        if (!isValidated) {
            return;
        }
        LoginAccountUsingFirebase(email, password);
    }

    void LoginAccountUsingFirebase(String email, String password) {
        changeInProgress(true);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if (task.isSuccessful()) {
                            if (auth.getCurrentUser().isEmailVerified()) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Email is not verified, Please verify your email.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email is Invalid");
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("Password length should be greater than 6");
            return false;
        }
        return true;
    }


}