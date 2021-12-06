package com.example.reminderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class setName extends AppCompatActivity {

    private TextView helloUser;
    private EditText editName;
    private Button bt_sb;
    private DBHandler dbHandler;
    public static String usersname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);

        helloUser=findViewById(R.id.helloUser);
        editName=findViewById(R.id.editName);
        bt_sb=findViewById(R.id.bt_sb);

        dbHandler= new DBHandler(setName.this);

        bt_sb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usersname=editName.getText().toString();
                dbHandler.setUserName(SignUp.phone,usersname);
                System.out.println("Name added to database");
                Toast.makeText(setName.this, "Logging into newly created account.", Toast.LENGTH_SHORT).show();
                Intent intent7= new Intent(setName.this,WelcomePage.class);
                startActivity(intent7);
            }
        });
    }
}