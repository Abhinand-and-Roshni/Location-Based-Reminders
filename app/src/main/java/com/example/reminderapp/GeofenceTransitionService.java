package com.example.reminderapp;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;

import java.util.ArrayList;
import java.util.List;

public class GeofenceTransitionService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GeofenceTransitionService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            String error = String.valueOf(geofencingEvent.getErrorCode());
            Toast.makeText(GeofenceTransitionService.this, "Error code - " + error, Toast.LENGTH_SHORT).show();
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String geoTransDetails = getGeofenceTransitionDetails(geofenceTransition, triggeringGeofences);

        }



    }

    private String getGeofenceTransitionDetails(int geofenceTransition, List<Geofence> triggeringGeofences) {

        ArrayList<String> triggerfencelist = new ArrayList<>();
        for(Geofence geofence: triggeringGeofences){
            triggerfencelist.add(geofence.getRequestId());

        }

        String status = null;
        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            status = "ENTERING";
        }
        else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            status = "EXITING";

        }
        return status + TextUtils.join(", ", triggerfencelist);
    }
}
