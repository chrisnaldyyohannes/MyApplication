package com.example.myapplication.password;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextView tvMessage;
    private LinearLayout llResetPassword,llMessage;

    private Button btnRetry,btnCancel;
    private  View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etEmail = findViewById(R.id.etEmail);
        tvMessage = findViewById(R.id.tvMessage);
        llMessage = findViewById(R.id.llMessage);
        llResetPassword = findViewById(R.id.llResetPassword);
        btnRetry = findViewById(R.id.btnRetry);
        btnCancel = findViewById(R.id.btnClose);
        progressBar = findViewById(R.id.progressBar);
    }
    public void btnReset(View view){
        final String email = etEmail.getText().toString().trim();
        if (email.equals("")){
            etEmail.setError(getString(R.string.enter_email));
        }else {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.GONE);
                    llResetPassword.setVisibility(View.GONE);
                    llMessage.setVisibility(View.VISIBLE);
                    if (task.isSuccessful()){
                        tvMessage.setText(getString(R.string.reset_password_instructions,email));
                        new CountDownTimer(60000,1000){

                            @SuppressLint("StringFormatMatches")
                            @Override
                            public void onTick(long millisUntilFinished) {
                                    btnRetry.setText(getString(R.string.resend_timer,String.valueOf(millisUntilFinished/1000)));
                                    btnRetry.setOnClickListener(null);
                            }

                            @Override
                            public void onFinish() {
                                    btnRetry.setText(R.string.retry);
                                    btnRetry.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            llResetPassword.setVisibility(View.VISIBLE);
                                            llMessage.setVisibility(View.GONE);
                                        }
                                    });
                            }
                        }.start();
                    }else {
                        tvMessage.setText(getString(R.string.email_sent_failed,task.getException()));
                        btnRetry.setText(R.string.retry);
                        btnRetry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                llResetPassword.setVisibility(View.VISIBLE);
                                llMessage.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        }
    }
    public void btClose(View view){
        finish();
    }
}