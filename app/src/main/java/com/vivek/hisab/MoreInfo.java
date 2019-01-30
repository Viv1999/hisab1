package com.vivek.hisab;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class MoreInfo extends AppCompatActivity implements View.OnClickListener {

    private static final int CHOOSE_IMAGE = 101;
    private EditText username;
    private ImageView profPic;
    private Button butSave;
    private FirebaseAuth mAuth;
    Uri uriProfImg;
    private ProgressBar progressBar;
    String profileImgUrl;
    FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_more_info);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.username);
        profPic = findViewById(R.id.profpicset);
        butSave = findViewById(R.id.saveDet);
        progressBar =  findViewById(R.id.progressBar);

        FirebaseUser user = mAuth.getCurrentUser();

        profPic.setOnClickListener(this);
        butSave.setOnClickListener(this);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){

            uriProfImg = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfImg);
                profPic.setImageBitmap(bitmap);
                profPic.setBackgroundResource(R.color.white);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            uploadImageToFirebaseStorage();


        }
    }

    private void uploadImageToFirebaseStorage() {

        final StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");
        if(uriProfImg != null) {
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    progressBar.setVisibility(View.GONE);
                    profileImgUrl = profileImageRef.getDownloadUrl().toString();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MoreInfo.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {

        if(v == profPic){

            showImageChooser();

        }
        if(v == butSave){
            saveUserInformation();
        }

    }

    private void saveUserInformation() {

        String displayName = username.getText().toString();
        if(displayName.isEmpty()){
            username.setError("Please enter username");
            username.requestFocus();
            return;
        }

        user = mAuth.getCurrentUser();
        if(user!=null && profileImgUrl!=null){
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImgUrl))
                    .build();
            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(MoreInfo.this,"Successfully Added",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MoreInfo.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        finish();
        startActivity(new Intent(this,Profile2Activity.class));
    }


    private void showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent,"Select Profile Image"),CHOOSE_IMAGE);
    }
}
