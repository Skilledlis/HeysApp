package com.example.heysapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private Button sendSmsButton, verificationButton, registerButton;
    private EditText phoneEditText, verificationEditText;
    private TextView tv2;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sendSmsButton = (Button) findViewById(R.id.send_sms_button);
        verificationButton = (Button) findViewById(R.id.sms_verification_button);
        registerButton = (Button)findViewById(R.id.register_button);
        phoneEditText = (EditText) findViewById(R.id.login_phone_input);
        verificationEditText = (EditText) findViewById(R.id.login_verification_input);
        tv2 = (TextView) findViewById(R.id.text_view_title2);
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent( LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        sendSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber = phoneEditText.getText().toString();
                if(TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(LoginActivity.this, "Введите свой номер телефона", Toast.LENGTH_LONG).show();
                }
                else {
                    loadingBar.setTitle("Проверка номера");
                    loadingBar.setMessage("Пожалуйста подождите");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            LoginActivity.this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks
                }
            }
        });

        verificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verificationCode = verificationEditText.getText().toString();
                if (TextUtils.isEmpty(verificationCode)){
                    Toast.makeText(LoginActivity.this, "Введите код", Toast.LENGTH_SHORT).show();
                }
                else {
                    loadingBar.setTitle("Проверка кода");
                    loadingBar.setMessage("Пожалуйста подождите");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingBar.dismiss();

                Toast.makeText(LoginActivity.this, "Ошибка номера", Toast.LENGTH_SHORT).show();
                sendSmsButton.setVisibility(View.VISIBLE);
                verificationButton.setVisibility(View.INVISIBLE);
                phoneEditText.setVisibility(View.VISIBLE);
                verificationEditText.setVisibility(View.INVISIBLE);
                tv2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                loadingBar.dismiss();

                mVerificationId = verificationId;
                mResendToken = token;

                Toast.makeText(LoginActivity.this, "Код отправлен", Toast.LENGTH_SHORT).show();
                sendSmsButton.setVisibility(View.INVISIBLE);
                verificationButton.setVisibility(View.VISIBLE);
                phoneEditText.setVisibility(View.INVISIBLE);
                verificationEditText.setVisibility(View.VISIBLE);
                tv2.setVisibility(View.INVISIBLE);
            }

        };
    }
   private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Проверка прошла успешно", Toast.LENGTH_SHORT).show();
                            Intent mainIntent = new Intent( LoginActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Ошибка проверки номера", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    };

}