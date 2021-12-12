package com.example.reminderapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;

public class selectLocation extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    boolean isPermissionGranted;
    GoogleMap mGoogleMap;
    ImageView img_search_icon;
    EditText et_location, et_rem;
    Button btn_add_rem;
    double lat1, long1; //marker dropped
    double lat2, long2; //current loc
    String lat1s, long1s;
    String str_loc;
    private DBHandler dbHandler;
    Marker mHere;
    LocationManager locationManager;
    public float distance[];
    Cursor cursor;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder notificationBuilder;

    public String showToasts=" ";

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(selectLocation.this);
        builder.setMessage("You will have to re-fill the form");
        builder.setTitle("Are you sure?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(selectLocation.this, WelcomePage.class));
                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        distance = new float[2];

        //here /* code for notif only once*/
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Notification compat = builder.build();
        compat.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        //here /* code for notif only once*/


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



        notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(selectLocation.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(selectLocation.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(selectLocation.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } // required for location manager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) selectLocation.this);

        img_search_icon = findViewById(R.id.img_search_icon);
        et_location = findViewById(R.id.et_location); //USER SPECIFIED LOCATION (dont store in db - only store long and lat)
        et_rem = findViewById(R.id.et_rem); //USER SPECIFIED REMINDER
        btn_add_rem = findViewById(R.id.btn_add_rem);

        dbHandler = new DBHandler(selectLocation.this);


        checkMyPermissions(); //location settings permission (access or deny)

        if (isPermissionGranted) {
            if (checkGooglePlayServices()) {
                Toast.makeText(this, "Google PlayServices are available", Toast.LENGTH_SHORT).show();
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag_map);
                supportMapFragment.getMapAsync(this);
            } else {
                Toast.makeText(this, "Google PlayServices are not available", Toast.LENGTH_SHORT).show();
            }
        }


        img_search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = et_location.getText().toString();
                if (location == null) {
                    Toast.makeText(selectLocation.this, "Type a valid location!", Toast.LENGTH_SHORT).show();
                } else {
                    Geocoder geocoder = new Geocoder(selectLocation.this, Locale.getDefault());
                    try {
                        List<Address> listAddress = geocoder.getFromLocationName(location, 1);
                        if (listAddress.size() > 0) {
                            LatLng latLng = new LatLng(listAddress.get(0).getLatitude(), listAddress.get(0).getLongitude());

                            lat1 = listAddress.get(0).getLatitude(); //LATITUDE OF THE REMINDER LOCATION
                            long1 = listAddress.get(0).getLongitude(); //LONGITUDE OF THE REMINDER LOCATION

                            lat1s = Double.toString(lat1); //for toast msg
                            long1s = Double.toString(long1); //for toast msg
                            Geocoder geocoder2 = new Geocoder(selectLocation.this, Locale.getDefault());
                            try{
                                List<Address>addressList1 = geocoder2.getFromLocation(lat1, long1,1);
                                if(addressList1.size()>0){
                                    str_loc = et_location.getText().toString() + " " + addressList1.get(0).getThoroughfare() + " " + addressList1.get(0).getSubThoroughfare() + " " + addressList1.get(0).getLocality() + " " + addressList1.get(0).getSubLocality() + " " + addressList1.get(0).getFeatureName()  + " " + addressList1.get(0).getCountryName() + " " + addressList1.get(0).getPostalCode();
                                    Toast.makeText(selectLocation.this, str_loc, Toast.LENGTH_SHORT).show();
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                                Toast.makeText(selectLocation.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                            //Toast.makeText(selectLocation.this, lat1s + " & " + long1s, Toast.LENGTH_SHORT).show();

                            if (mHere != null) {
                                mHere.remove();
                            }
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.title("Here");
                            markerOptions.position(latLng);
                            mHere = mGoogleMap.addMarker(markerOptions);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 5);
                            mGoogleMap.animateCamera(cameraUpdate);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btn_add_rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_rem.getText().toString().length() == 0) {
                    Toast.makeText(selectLocation.this, "Enter valid reminder!", Toast.LENGTH_SHORT).show();
                } else if (lat1s == null || long1s == null) {
                    Toast.makeText(selectLocation.this, "Please select a valid location", Toast.LENGTH_SHORT).show();
                }

                //code for adding details to database table - user's reminder
                else {
                    dbHandler.addReminderRecord(et_rem.getText().toString(), str_loc, lat1s, long1s);
                    Toast.makeText(selectLocation.this, "Reminder stored", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(selectLocation.this, WelcomePage.class));

                }
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
                    Toast.makeText(selectLocation.this, "User cancelled", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(selectLocation.this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
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


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);


    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        lat2 = location.getLatitude(); //current lat
        long2 = location.getLongitude(); //current long

//        selectLocation.ConnectMySql connectMySql = new selectLocation.ConnectMySQL();
//        connectMySql.execute("");



    }

    private class ConnectToDB extends AsyncTask<String, Void, String>{
        int i=1;
        String resz = "";
        String resz1 = "";
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(String... params) {
            try {
                showToasts=" ";
                resz="";
                resz1="";
//                Class.forName("com.mysql.jdbc.Driver");
                Connection con= DriverManager.getConnection(/*url,user,pass*/ "", "", "");
//                System.out.println("Database connection success!");

                String result="";
                String result2="";
              Statement st=con.createStatement();

                String currentlat,currentlong;

              ResultSet rs=st.executeQuery("SELECT `LATITUDE`, `LONGITUDE`, `USERNAME` FROM `info_users_table`");

                ResultSetMetaData rsmd = rs.getMetaData();
                while(rs.next())
                {
                    currentlat=rs.getString(1).toString();
                    currentlong=rs.getString(2).toString();

                    Location.distanceBetween(lat2,long2,lat1,lat1,distance);

                    if(distance[0]<=1000)

                    {
                        //s=true;
                        System.out.println("You are in the location of "+rs.getString(3).toString());

                        showToasts="You are in the location of "+rs.getString(3).toString();
                    }

                    result+=(String.valueOf(i)+") "+rs.getString(1).toString()+ "\n");
                    result2+=(String.valueOf(i)+") "+rs.getString(2).toString()+"\n");
                    i++;
                }
                resz=result;
                resz1=result2;


            } catch(Exception e)
            {
                e.printStackTrace();
                resz=e.toString();
            }
            return resz;
        }


        @Override
        protected void onPostExecute(String s) {

            if(showToasts!=" ") {
                Toast.makeText(selectLocation.this, "Hey there", Toast.LENGTH_SHORT).show();
                notificationBuilder.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setTicker("Hearty365")
                        .setContentTitle("REMINDER ALERT!")
                        .setContentText(showToasts)
                        .setContentInfo("Info");
                mNotificationManager.notify(/*notification id*/1, notificationBuilder.build());

            }

        }
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


}