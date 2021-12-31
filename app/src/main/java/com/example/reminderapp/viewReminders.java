package com.example.reminderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class viewReminders extends AppCompatActivity {


    private ArrayList<reminderDetails> reminderDetailsArrayList;
    private DBHandler dbHandler;
    private ReminderRVAdapter reminderRVAdapter;
    private RecyclerView remindersRV;
    TextView txt_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reminders);

        txt_empty = findViewById(R.id.txt_empty);

        reminderDetailsArrayList=new ArrayList<>();
        dbHandler=new DBHandler(viewReminders.this);

        reminderDetailsArrayList=dbHandler.readReminders(SaveSharedPreference.getPhoneNo(viewReminders.this));
        if(reminderDetailsArrayList.isEmpty()){
            txt_empty.setText("Nothing to see here.\nAdd a reminder first!");
            //Toast.makeText(viewReminders.this, "Add a reminder first :)", Toast.LENGTH_SHORT).show();
        }
        reminderRVAdapter=new ReminderRVAdapter(reminderDetailsArrayList,viewReminders.this);
        remindersRV=findViewById(R.id.idRVReminders);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(viewReminders.this,RecyclerView.VERTICAL,false);
        remindersRV.setLayoutManager(linearLayoutManager);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(remindersRV);

        remindersRV.setAdapter(reminderRVAdapter);

    }
    //roshni working on deleting slide feature
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            reminderDetailsArrayList.remove(viewHolder.getAdapterPosition());


            reminderRVAdapter.notifyDataSetChanged();

        }
    };
}