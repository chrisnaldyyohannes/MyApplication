package com.example.myapplication.password;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText etPassword,etComfirmPasswoed;
    private  View progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etPassword = findViewById(R.id.editTextPass);
        etComfirmPasswoed = findViewById(R.id.editTextPassConfirm);
        progressBar = findViewById(R.id.progressBar);
    }
    public void btnChangePassword(View view){
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etComfirmPasswoed.getText().toString().trim();

        if (password.equals("")){
            etPassword.setError("empty");
        }else if (confirmPassword.equals("")){
            etComfirmPasswoed.setError("empty");
        }else if (!password.equals(confirmPassword)){
            etComfirmPasswoed.setError("not match");
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser !=null){
                firebaseUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()){
                            Toast.makeText(ChangePasswordActivity.this,"sukses",Toast.LENGTH_LONG).show();
                            finish();
                        }else {
                            Toast.makeText(ChangePasswordActivity.this,"fail",Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        }
    }
}