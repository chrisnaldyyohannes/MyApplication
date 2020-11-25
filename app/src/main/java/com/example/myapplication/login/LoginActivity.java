package com.example.myapplication.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.MessageActivity;
import com.example.myapplication.R;
import com.example.myapplication.common.Util;
import com.example.myapplication.password.ChangePasswordActivity;
import com.example.myapplication.password.ResetPasswordActivity;
import com.example.myapplication.register.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private String email, password;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.editTextUsername);
        etPassword= findViewById(R.id.editTextPass);
        progressBar = findViewById(R.id.progressBar);
    }
    public void tvCreateNew(View v){
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }
    public void btnLoginClick(View v){
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        if (email.equals("")){
            etEmail.setError(getString(R.string.enter_email));
        }else if (password.equals("")){
            etPassword.setError(getString(R.string.enter_password));
        }else {
            if (Util.connectionCheck(this)) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed : " + task.getException(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }else {
                startActivity(new Intent(LoginActivity.this, MessageActivity.class));
            }
        }
    }
    public void tvResetPassword(View view){
        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser!=null){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }
}