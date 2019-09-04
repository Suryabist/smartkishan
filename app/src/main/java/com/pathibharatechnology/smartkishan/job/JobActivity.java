package com.pathibharatechnology.smartkishan.job;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.SupportActionBarInitializer;

import java.util.ArrayList;
import java.util.List;

public class JobActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    JobDTO jobDTO;
    List<JobDTO> jobDTOList;
    JobAdapter adapter;
    FloatingActionButton addJobButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        Toolbar toolbar = findViewById(R.id.toolbarId);
        setSupportActionBar(toolbar);
        SupportActionBarInitializer.setUpSupportActionBar(getSupportActionBar(), "Jobs", true);
        addJobButton = findViewById(R.id.addJobId);

        jobDTOList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewID);
        jobDetails();

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);


        adapter = new JobAdapter(jobDTOList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);


        addJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(JobActivity.this, "Under construction...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void jobDetails(){

        jobDTO = new JobDTO("Manager IE(Industrial Engineer)", "ABC agro", "Gaushala", (long) 10000, "Analyzing the CMT and Productivity details for all new development styles based on garment sketch or samples and provide CMT to merchandising department.", "2019-4-4");
        jobDTOList.add(jobDTO);

        jobDTO = new JobDTO("Area Sales Manager", "Test company", "Mitrapark", (long) 30000, "To plan, strategize and implement the sales programs in a particular region. Developing a sales plan, set a target for the salespersons, organizing them, and implement the strategies devised to increase the revenues.", "2019-4-9");
        jobDTOList.add(jobDTO);

        JobDTO jobDTO = new JobDTO("Engineer Quality Control(control Panel)", "Loot company", "Ratnapark", (long) 30000, "Cold testing and functional testing of the panels as per standard and customer requirement" +
                " Providing on call support to the customers for commissioning of the panel", "2019-10-4");
        jobDTOList.add(jobDTO);

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
