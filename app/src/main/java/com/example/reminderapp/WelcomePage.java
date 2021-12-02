package com.example.reminderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reminderapp.SignUp;
public class WelcomePage extends AppCompatActivity {

    private TextView helloUserView;
    private String usersname;
    private DBHandler dbHandler;
    private Button button1,button2,button3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        dbHandler= new DBHandler(WelcomePage.this);


        usersname=dbHandler.findUserName(SignUp.phone);
        helloUserView=findViewById(R.id.helloUserView);
        helloUserView.setText("Hey there, "+usersname);

        button1=findViewById(R.id.button);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomePage.this, MainActivity.class));
                Toast.makeText(WelcomePage.this, "Logged out!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}