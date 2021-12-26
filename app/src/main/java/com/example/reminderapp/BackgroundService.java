package com.example.reminderapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;


public class BackgroundService extends Service implements LocationListener {

    private DBHandler dbHandler;
    double lat2, long2; //current loc
    LocationManager locationManager;
    public static float distance[];
    NotificationManager mNotificationManager;
    NotificationCompat.Builder notificationBuilder;
    public static String showToasts = " ";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent,flags,startId);
    }


    @Override
    public void onCreate()
    {

        System.out.println("Service STARTEDDDDD");

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        dbHandler = new DBHandler(BackgroundService.this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", mNotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }


        notificationBuilder = new NotificationCompat.Builder(BackgroundService.this, NOTIFICATION_CHANNEL_ID);


        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setTicker("Hearty365")
                .setContentTitle("Reminder App is Running")
                .setContentText("Disable GPS permissions to prevent this.")
                .setContentInfo("Info");


        startForeground(1,notificationBuilder.build());

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(BackgroundService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ;

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 0.0f, 0.0f, (LocationListener) this);
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    @Override
    public void onLocationChanged(@NonNull Location location) {

        if(lat2!=location.getLatitude() && long2!=location.getLongitude())
        {
            lat2 = location.getLatitude(); //current lat
            long2 = location.getLongitude(); //current long

            System.out.println("Location changed function called!");

            //Toast.makeText(this, "location changed a bit", Toast.LENGTH_SHORT).show();
            boolean answer_needed=dbHandler.checkIfInRange(lat2,long2,SaveSharedPreference.getPhoneNo(BackgroundService.this));

            if(answer_needed) {

                System.out.println("Hey there, "+dbHandler.findUserName(SaveSharedPreference.getPhoneNo(BackgroundService.this))+ ". You have a reminder!");


                notificationBuilder.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setTicker("Hearty365")
                        .setContentTitle("Hey there, "+dbHandler.findUserName(SaveSharedPreference.getPhoneNo(BackgroundService.this))+ ". You have a reminder!")
                        .setContentText(showToasts)
                        .setContentInfo("Info");

               mNotificationManager.notify(2, notificationBuilder.build());

               //Toast.makeText(BackgroundService.this, "Reminder notification sent!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}
