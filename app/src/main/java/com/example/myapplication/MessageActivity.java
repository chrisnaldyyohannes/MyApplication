package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myapplication.common.Util;

public class MessageActivity extends AppCompatActivity {

    private TextView tvMessages;
    private ProgressBar progressBar;
    private ConnectivityManager.NetworkCallback networkCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        tvMessages = findViewById(R.id.tvMessages);
        progressBar = findViewById(R.id.progressBar);

        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP){
            networkCallback = new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    finish();
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    tvMessages.setText(getString(R.string.no_internet));
                }
            };
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            connectivityManager.registerNetworkCallback(new NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build(),networkCallback);
        }
    }
    public void btnRetry(View view){
        progressBar.setVisibility(View.VISIBLE);
        if (Util.connectionCheck(this)){
            finish();
        }else {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                }
            },1000);
        }
    }
    public void btnClose(View view){
        finishAffinity();
    }
}