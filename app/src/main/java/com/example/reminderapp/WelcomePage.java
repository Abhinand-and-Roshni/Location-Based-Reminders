package com.example.reminderapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;

import static androidx.core.app.ServiceCompat.stopForeground;

public class WelcomePage extends AppCompatActivity  {

    private TextView helloUserView;
    private String usersname;
    private DBHandler dbHandler;
    private Button btn_create_rem, btn_view_rem, btn_logout;
    public static float distance[];


    @Override
    public void onBackPressed() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        startService(new Intent(WelcomePage.this, BackgroundService.class));


        dbHandler = new DBHandler(WelcomePage.this);


        usersname = dbHandler.findUserName(SaveSharedPreference.getPhoneNo(WelcomePage.this));
        helloUserView = findViewById(R.id.helloUserView);
        helloUserView.setText("Hey there, " + usersname + "!");

        btn_create_rem = findViewById(R.id.btn_create_rem);
        btn_view_rem = findViewById(R.id.btn_view_rem);
        btn_logout = findViewById(R.id.btn_logout);

        distance = new float[2];



        btn_view_rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(WelcomePage.this, "VIEWING REMINDERS!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(WelcomePage.this, viewReminders.class));
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WelcomePage.this);
                builder.setMessage("Are you sure you want to log out?");
                builder.setTitle("Alert!");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(WelcomePage.this, MainActivity.class));
                        SaveSharedPreference.clearPhoneNo(WelcomePage.this);
                        Toast.makeText(WelcomePage.this, "User logged out.", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                //Toast.makeText(WelcomePage.this, "Create reminder selected", Toast.LENGTH_SHORT).show();
            }
        });

    }





}