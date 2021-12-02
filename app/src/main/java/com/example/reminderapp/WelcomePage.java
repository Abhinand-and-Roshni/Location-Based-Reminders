package com.example.reminderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomePage extends AppCompatActivity {

    private TextView helloUserView;
    private String usersname;
    private DBHandler dbHandler;
    private Button btn_create_rem,btn_view_rem,btn_logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        dbHandler= new DBHandler(WelcomePage.this);


        usersname=dbHandler.findUserName(SignUp.phone);
        helloUserView=findViewById(R.id.helloUserView);
        helloUserView.setText("Hey there, "+usersname + "!");

        btn_create_rem=findViewById(R.id.btn_create_rem);
        btn_view_rem=findViewById(R.id.btn_view_rem);
        btn_logout=findViewById(R.id.btn_logout);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomePage.this, MainActivity.class));
                Toast.makeText(WelcomePage.this, "Logged out!", Toast.LENGTH_SHORT).show();
            }
        });

        btn_create_rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomePage.this, selectLocation.class));
                Toast.makeText(WelcomePage.this, "Create reminder selected", Toast.LENGTH_SHORT).show();
            }
        });

    }
}