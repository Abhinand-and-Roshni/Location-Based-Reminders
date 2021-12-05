package com.example.reminderapp;

import android.content.Context;
import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReminderRVAdapter extends RecyclerView.Adapter<ReminderRVAdapter.ViewHolder> {
    private ArrayList<reminderDetails> reminderDetailsArrayList;
    private Context context;

    public ReminderRVAdapter(ArrayList<reminderDetails> reminderDetailsArrayList, Context context) {
        this.reminderDetailsArrayList = reminderDetailsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        reminderDetails modal = reminderDetailsArrayList.get(position);
        holder.reminderNameTV.setText(modal.getReminderName());
        holder.reminderPlaceTV.setText("Location: "+modal.getReminderLocation());
        holder.latitudeTV.setText("Latitude: "+modal.getLatitude());
        holder.longitudeTV.setText("Longitude: "+modal.getLongitude());
    }

    @Override
    public int getItemCount() {
        return reminderDetailsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView reminderNameTV, reminderPlaceTV, latitudeTV, longitudeTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reminderNameTV = itemView.findViewById(R.id.idReminderName);
            reminderPlaceTV = itemView.findViewById(R.id.idReminderPlace);
            latitudeTV = itemView.findViewById(R.id.idLatitude);
            longitudeTV = itemView.findViewById(R.id.idLongitude);
        }
    }
}
