package com.example.myapplication.register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.common.NodeName;
import com.example.myapplication.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Node;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText etEmail,etName,etPasswprd,etConfPassword;
    private String email,name,password,confPassword;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private Uri localFileUri,serverFileUri;
    private ImageView imageView;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etEmail = findViewById(R.id.editTextUsername);
        etName = findViewById(R.id.editTextName);
        etPasswprd = findViewById(R.id.editTextPass);
        etConfPassword = findViewById(R.id.editTextPassConfirm);
        imageView = findViewById(R.id.ivProfile);
        progressBar = findViewById(R.id.progressBar);
        storageReference = FirebaseStorage.getInstance().getReference();

    }

    public void pickImage(View v){
        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,101);
        }else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},101);
        }
    }

    private void updateNameandPhoto(){
        String strFileName = firebaseUser.getUid() + ".jpg";
        final StorageReference fileRef = storageReference.child("image/"+strFileName);
        progressBar.setVisibility(View.VISIBLE);
        fileRef.putFile(localFileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            serverFileUri = uri;
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(etName.getText().toString().trim())
                                    .setPhotoUri(serverFileUri)
                                    .build();

                            firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        String userID = firebaseUser.getUid();
                                        databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeName.USERS);

                                        HashMap<String,String> hashMap = new HashMap<>();
                                        hashMap.put(NodeName.NAME,etName.getText().toString().trim());
                                        hashMap.put(NodeName.EMAIL,etEmail.getText().toString().trim());
                                        hashMap.put(NodeName.ONLINE,"true");
                                        hashMap.put(NodeName.PHOTO,serverFileUri.getPath());

                                        databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(SignUpActivity.this,"successful",Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                            }
                                        });
                                    }else {
                                        Toast.makeText(SignUpActivity.this,"failed to update",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void updateOnlyName(){
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
        .setDisplayName(etName.getText().toString().trim())
        .build();
        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    String userID = firebaseUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeName.USERS);

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put(NodeName.NAME,etName.getText().toString().trim());
                    hashMap.put(NodeName.EMAIL,etEmail.getText().toString().trim());
                    hashMap.put(NodeName.ONLINE,"true");
                    hashMap.put(NodeName.PHOTO,"");

                    databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(SignUpActivity.this,"successful",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        }
                    });
                }else {
                    Toast.makeText(SignUpActivity.this,"failed to update",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void btnSignUp(View v){
        email = etEmail.getText().toString().trim();
        name = etName.getText().toString().trim();
        password = etPasswprd.getText().toString().trim();
        confPassword = etConfPassword.getText().toString().trim();

        if (email.equals("")){
            etEmail.setError(getString(R.string.enter_email));
        }else if (name.equals("")){
            etName.setError("must be filled");
        }else if (password.equals("")){
            etPasswprd.setError(getString(R.string.enter_password));
        }else if (confPassword.equals("")){
            etConfPassword.setError(getString(R.string.enter_password));
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("enter correct email");
        }else if (!password.equals(confPassword)){
            etConfPassword.setError("not match");
        }else{
            progressBar.setVisibility(View.VISIBLE);
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()){
                        firebaseUser = firebaseAuth.getCurrentUser();
                        if (localFileUri!=null){
                            updateNameandPhoto();
                        }else {
                            updateOnlyName();
                        }
                    }else {
                        Toast.makeText(SignUpActivity.this,"fail : %1$s",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==102){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,101);
            }else {
                Toast.makeText(SignUpActivity.this,"permission denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101){
            if (resultCode==RESULT_OK){
                localFileUri = data.getData();
                imageView.setImageURI(localFileUri);
            }
        }
    }
}