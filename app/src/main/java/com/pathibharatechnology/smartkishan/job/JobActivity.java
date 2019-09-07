package com.pathibharatechnology.smartkishan.job;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.SupportActionBarInitializer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JobActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    JobDTO jobDTO;
    List<JobDTO> jobDTOList;
    JobAdapter adapter;
    FloatingActionButton addJobButton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        Toolbar toolbar = findViewById(R.id.toolbarId);
        setSupportActionBar(toolbar);
        SupportActionBarInitializer.setUpSupportActionBar(getSupportActionBar(), "Jobs", true);
        addJobButton = findViewById(R.id.addJobId);
        progressBar = findViewById(R.id.progressBarID);

        jobDTOList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewID);
        fetchJobsFromDatabase();

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);


        adapter = new JobAdapter(jobDTOList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);


        addJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JobActivity.this, AddJobActivity.class);
                startActivity(intent);
            }
        });

    }


    private void fetchJobsFromDatabase() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseDatabase.getInstance().getReference().child("jobs")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        jobDTOList = new ArrayList<>();
                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot snap = iterator.next();
                            jobDTOList.add(snap.getValue(JobDTO.class));

                        }

                        JobAdapter adapter = new JobAdapter(jobDTOList, JobActivity.this);

                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
