package com.example.reminderapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class viewReminders extends AppCompatActivity {


    private ArrayList<reminderDetails> reminderDetailsArrayList;
    private DBHandler dbHandler;
    private ReminderRVAdapter reminderRVAdapter;
    private RecyclerView remindersRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reminders);

        reminderDetailsArrayList=new ArrayList<>();
        dbHandler=new DBHandler(viewReminders.this);

        reminderDetailsArrayList=dbHandler.readReminders();
        reminderRVAdapter=new ReminderRVAdapter(reminderDetailsArrayList,viewReminders.this);
        remindersRV=findViewById(R.id.idRVReminders);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(viewReminders.this,RecyclerView.VERTICAL,false);
        remindersRV.setLayoutManager(linearLayoutManager);

        remindersRV.setAdapter(reminderRVAdapter);

    }
}