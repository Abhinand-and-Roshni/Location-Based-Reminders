package com.example.reminderapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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


    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(WelcomePage.this);
        builder.setMessage("Are you sure you want to log out?");
        builder.setTitle("Alert!");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int which){
                startActivity(new Intent(WelcomePage.this,MainActivity.class));
            }
        });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which){
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

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


        btn_view_rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(WelcomePage.this, "VIEWING REMINDERS!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(WelcomePage.this,viewReminders.class));
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WelcomePage.this);
                builder.setMessage("Are you sure you want to log out?");
                builder.setTitle("Alert!");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which){
                        startActivity(new Intent(WelcomePage.this, MainActivity.class));
                        Toast.makeText(WelcomePage.this, "Logged out!", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
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