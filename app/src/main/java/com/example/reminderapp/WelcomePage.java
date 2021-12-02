package com.example.reminderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import com.example.reminderapp.SignUp;
public class WelcomePage extends AppCompatActivity {

    private TextView helloUserView;
    private String usersname;
    private DBHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        dbHandler= new DBHandler(WelcomePage.this);


        usersname=dbHandler.findUserName(SignUp.phone);
        helloUserView=findViewById(R.id.helloUserView);
        helloUserView.setText("Hey there, "+usersname);
    }
}