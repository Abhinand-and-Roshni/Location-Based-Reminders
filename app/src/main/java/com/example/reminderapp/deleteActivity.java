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

public class deleteActivity extends AppCompatActivity {

    private TextView remNameEdt, remLocEdt, remLatEdt, remLongEdt;
    private DBHandler dbHandler;
    private Button deleteBtn;
    String remName,remLoc,remLat,remLong;

    public void onBackPressed(){
        startActivity(new Intent(deleteActivity.this,WelcomePage.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        remNameEdt=findViewById(R.id.remNameEdt);
        remLocEdt=findViewById(R.id.remLocEdt);
        remLatEdt=findViewById(R.id.remLatEdt);
        remLongEdt=findViewById(R.id.remLongEdt);

        deleteBtn=findViewById(R.id.deleteBtn);

        dbHandler=new DBHandler(deleteActivity.this);


        remName = getIntent().getStringExtra("Reminder Name");
        remLoc = getIntent().getStringExtra("Location");
        remLat = getIntent().getStringExtra("Latitude");
        remLong = getIntent().getStringExtra("Longitude");

        remNameEdt.setText(remName);
        remLocEdt.setText(remLoc);
        remLatEdt.setText(remLat);
        remLongEdt.setText(remLong);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(deleteActivity.this);
                builder.setMessage("Are you sure you want to delete this reminder?");
                builder.setTitle("Alert!");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                dbHandler.deleteReminder(remName,SaveSharedPreference.getPhoneNo(deleteActivity.this));
                                Toast.makeText(deleteActivity.this, "Reminder deleted!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(deleteActivity.this,viewReminders.class));
                            }
                        });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which){
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
}