package com.example.heysapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SittingsActivity extends AppCompatActivity {

    private Button saveInfBtn;
    private EditText userNameET, statusET;
    private CircleImageView circleImageView;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private static final int GALLERY_PICK = 1;
    private StorageReference UserProfileImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sittings);

        saveInfBtn = findViewById(R.id.save_user_inf);
        userNameET = findViewById(R.id.set_user_name);
        statusET = findViewById(R.id.set_user_status);
        circleImageView = findViewById(R.id.profile_image);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        userNameET.setVisibility(View.INVISIBLE);
        
        saveInfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateInformation();
            }
        });

        retrieveUserInformation();

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PICK);
            }
        });
    }


    private void UpdateInformation() {
        String setName = userNameET.getText().toString();
        String setStatus = statusET.getText().toString();

        if (TextUtils.isEmpty(setName)){
            Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setStatus)){
            Toast.makeText(this, "Заполните поле статус", Toast.LENGTH_SHORT).show();
        }else {
            HashMap<String , Object> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("name", setName);
            profileMap.put("status", setStatus);

            rootRef.child("Users").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(SittingsActivity.this,"Информация обновлена", Toast.LENGTH_SHORT).show();

                                Intent mainIntent = new Intent(SittingsActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                            }
                            else {
                                String message = Objects.requireNonNull(task.getException()).toString();
                                Toast.makeText(SittingsActivity.this, "Произошла ошибка: "+ message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GALLERY_PICK && resultCode==RESULT_OK && data!=null){
            Uri imageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode==RESULT_OK){
            Uri resultUri = result.getUri();
            StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");
            filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SittingsActivity.this, "Your profile image saved", Toast.LENGTH_SHORT).show();
                    }else {
                        String massage = task.getException().toString();
                        Toast.makeText(SittingsActivity.this, "Error: " + massage, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            }
        }
    }

    private void retrieveUserInformation() {
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("name")){
                    String retrieveUserName = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                    String retrieveUserStatus = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();

                    userNameET.setText(retrieveUserName);
                    statusET.setText(retrieveUserStatus);

                }
                else {
                    userNameET.setVisibility(View.VISIBLE);
                    Toast.makeText(SittingsActivity.this, "Введите своё имя", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}