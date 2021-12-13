package com.example.reminderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;



public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editPhone, editOTP;
    private Button verifyOTPBtn, generateOTPBtn;


    private String verificationId;
    public  static String phone;

    private DBHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth=FirebaseAuth.getInstance();
        editOTP=findViewById(R.id.idEdtOtp);
        editPhone=findViewById(R.id.idEdtPhoneNumber);
        verifyOTPBtn=findViewById(R.id.idBtnVerify);
        generateOTPBtn=findViewById(R.id.idBtnGetOtp);

        dbHandler= new DBHandler(SignUp.this);

        generateOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Phone number entered..");
                if(editPhone.length()==10){
                    phone="+91"+editPhone.getText().toString();
                    SaveSharedPreference.setPhoneNo(SignUp.this,phone);
                    sendVerificationCode(phone);
                }
                else{
                    Toast.makeText(SignUp.this, "Enter valid phone number!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        verifyOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    verifyCode(editOTP.getText().toString());
                }catch(Exception e){
                    Toast.makeText(SignUp.this, "Verify your number via OTP first!", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }
    private void signInWithCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    editOTP.setText("");
                    editPhone.setText("");

                    //Toast.makeText(SignUp.this, "CORRECT CREDENTIAL", Toast.LENGTH_SHORT).show();


                    if(!dbHandler.checkRecord(phone)) {
                        System.out.println("New record");
                        dbHandler.addNewRecord(phone);
                        Toast.makeText(SignUp.this, "Welcome!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUp.this, setName.class));

                    }
                    else{
                        System.out.println("Record exists");
                        //Toast.makeText(SignUp.this, "Logging into existing account.", Toast.LENGTH_SHORT).show();

                        if(!dbHandler.checkUserName(phone)){
                            startActivity( new Intent(SignUp.this, setName.class));
                        }
                        else{
                            Toast.makeText(SignUp.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUp.this,WelcomePage.class));
                        }

                    }

                }
                else{
                    Toast.makeText(SignUp.this, "Invalid OTP!", Toast.LENGTH_SHORT).show();
                    editOTP.setText("");
                }

            }
        });
    }

    private void sendVerificationCode(String number)
    {
        System.out.println("Inside sendVerificaitonCode..");
        PhoneAuthOptions options=PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBack)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            Toast.makeText(SignUp.this, "OTP has been sent!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            final String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                editOTP.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            System.out.println(e);

        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }




}