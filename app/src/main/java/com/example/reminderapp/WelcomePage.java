package com.example.reminderapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

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

public class WelcomePage extends AppCompatActivity implements LocationListener {

    private TextView helloUserView;
    private String usersname;
    private DBHandler dbHandler;
    private Button btn_create_rem,btn_view_rem,btn_logout;
    double lat2, long2; //current loc
    LocationManager locationManager;
    public static float distance[];
    NotificationManager mNotificationManager;
    NotificationCompat.Builder notificationBuilder;
    public static String showToasts=" ";
    boolean isPermissionGranted;

    //ROSHNI BALASUBRAMANIAN BALANEHRU JEEVA



    public void onBackPressed()
    {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        dbHandler= new DBHandler(WelcomePage.this);


        usersname=dbHandler.findUserName(SaveSharedPreference.getPhoneNo(WelcomePage.this));
        helloUserView=findViewById(R.id.helloUserView);
        helloUserView.setText("Hey there, "+usersname + "!");

        btn_create_rem=findViewById(R.id.btn_create_rem);
        btn_view_rem=findViewById(R.id.btn_view_rem);
        btn_logout=findViewById(R.id.btn_logout);

        distance = new float[2];



        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String NOTIFICATION_CHANNEL_ID="my_channel_id_01";


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", mNotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        checkMyPermissions();

        if (isPermissionGranted) {
            if (checkGooglePlayServices()) {
                //Toast.makeText(this, "Google PlayServices are available", Toast.LENGTH_SHORT).show();
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag_map);
            } else {
                System.err.println("Google play services are not available!");
                //Toast.makeText(this, "Google PlayServices are not available", Toast.LENGTH_SHORT).show();
            }
        }

        notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);


        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);



        if(ActivityCompat.checkSelfPermission(WelcomePage.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED);
        {
            ActivityCompat.requestPermissions(WelcomePage.this,new String[] { Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 0.0f,0.0f, (LocationListener) this);
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);




        btn_view_rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(WelcomePage.this, "VIEWING REMINDERS!!", Toast.LENGTH_SHORT).show();
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
                        SaveSharedPreference.clearPhoneNo(WelcomePage.this);
                        Toast.makeText(WelcomePage.this, "User logged out.", Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(WelcomePage.this, "Create reminder selected", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if(result == ConnectionResult.SUCCESS){
            return true;
        }else if(googleApiAvailability.isUserResolvableError(result)){
            Dialog dialog = googleApiAvailability.getErrorDialog(this, result, 201, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    //Toast.makeText(WelcomePage.this, "User cancelled", Toast.LENGTH_SHORT).show();
                    System.out.println("User cancelled action.");
                }
            });
            dialog.show();
        }
        return false;
    }

    private void checkMyPermissions() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                isPermissionGranted = true;
                //Toast.makeText(WelcomePage.this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
                System.out.println("Location permissions have been granted");
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent_settings = new Intent();
                intent_settings.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent_settings.setData(uri);
                startActivity(intent_settings);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(lat2!=location.getLatitude() && long2!=location.getLongitude())
        {
            lat2 = location.getLatitude(); //current lat
            long2 = location.getLongitude(); //current long

            System.out.println("Location changed function called!");

            //Toast.makeText(this, "location changed a bit", Toast.LENGTH_SHORT).show();
            boolean answer_needed=dbHandler.checkIfInRange(lat2,long2,SaveSharedPreference.getPhoneNo(WelcomePage.this));

            if(answer_needed==true) {

                notificationBuilder.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setTicker("Hearty365")
                        .setContentTitle("Hey, "+usersname + ". You have a reminder!")
                        .setContentText(showToasts)
                        .setContentInfo("Info");
                mNotificationManager.notify(1, notificationBuilder.build());

                //Toast.makeText(this, "Reminder notification sent!", Toast.LENGTH_SHORT).show();
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