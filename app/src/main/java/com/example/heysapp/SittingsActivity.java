package com.example.heysapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SittingsActivity extends AppCompatActivity {

    private Button saveInfBtn;
    private EditText userNameET, statusET;
    private CircleImageView circleImageView;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sittings);

        saveInfBtn = (Button)findViewById(R.id.save_user_inf);
        userNameET = (EditText)findViewById(R.id.set_user_name);
        statusET = (EditText)findViewById(R.id.set_user_status);
        circleImageView = (CircleImageView)findViewById(R.id.profile_image);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        
        saveInfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateInformation();
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
                                String message = task.getException().toString();
                                Toast.makeText(SittingsActivity.this, "Произошла ошибка: "+ message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}