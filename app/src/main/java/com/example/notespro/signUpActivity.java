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
import android.widget.Toast;

import com.example.notespro.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class signUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    EditText etEmail, etPassword;
    Button createAccount;
    EditText etConfirmPassword;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        etEmail = binding.etEmailUp;
        etPassword = binding.etPasswordUp;
        etConfirmPassword = binding.etConfirmPasswordUp;
        progressBar = binding.progressBar;
        createAccount = binding.createAccount;

        createAccount.setOnClickListener(v -> createAccountMethod());
        binding.alreadyLogin.setOnClickListener(v -> {
            Intent intent = new Intent(signUpActivity.this , LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }

    void createAccountMethod() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        boolean isValidated = validateData(email, password, confirmPassword);
        if (! isValidated){
            return;
        }

        createAccountUsingFirebase(email,password);

    }

    void createAccountUsingFirebase(String email, String password) {
        changeInProgress(true);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if (task.isSuccessful()){
                            Toast.makeText(signUpActivity.this, "Successfully create account.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(signUpActivity.this, "Check email to verify", Toast.LENGTH_LONG).show();
                            auth.getCurrentUser().sendEmailVerification();
                            auth.signOut();
                            finish();
                        }
                        else {
                            Toast.makeText(signUpActivity.this, task.getException().getLocalizedMessage() , Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    void changeInProgress(boolean inProgress){
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccount.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            createAccount.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password, String confirmPassword) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email is Invalid");
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("Password length should be greater than 6");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Password not matched");
            return false;
        }
        return true;
    }


}



