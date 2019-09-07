package com.pathibharatechnology.smartkishan.job;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.pathibharatechnology.smartkishan.R;

public class AddJobActivity extends AppCompatActivity {

    TextInputEditText titleText, companyText, locationText, salaryText, detailText, timeLimitText;
    Button submitButton;
    ProgressBar progressBar;
    String title, company, location, detail, timeLimit;
    Long salary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        titleText = findViewById(R.id.jobTitleTextId);
        companyText = findViewById(R.id.companyTextId);
        locationText = findViewById(R.id.locationTextId);
        salaryText = findViewById(R.id.salaryTextId);
        detailText = findViewById(R.id.detailsTextId);
        timeLimitText = findViewById(R.id.timeLimitTextId);
        submitButton = findViewById(R.id.uploadButtonID);
        progressBar = findViewById(R.id.progressBarId);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    addJobToDatabase();
                }
            }
        });


    }

    private boolean validate() {
        boolean isValid = false;
        title = titleText.getText().toString().trim();
        company = companyText.getText().toString().trim();
        location = locationText.getText().toString().trim();
        detail = detailText.getText().toString().trim();
        timeLimit = timeLimitText.getText().toString().trim();
        salary = Long.valueOf(salaryText.getText().toString());
        if (TextUtils.isEmpty(title)) {
            titleText.setError("Required");
        } else if (TextUtils.isEmpty(company)) {
            companyText.setError("Required");
        } else if (TextUtils.isEmpty(location)) {
            locationText.setError("Required");
        } else if (TextUtils.isEmpty(detail)) {
            detailText.setError("Required");
        } else if (TextUtils.isEmpty(timeLimit)) {
            timeLimitText.setError("Required");
        } else if (salaryText.getText().toString().equals("")) {
            salaryText.setError("Required");
        } else {
            isValid = true;

        }
        return isValid;
    }

    private void addJobToDatabase() {
        final JobDTO jobDTO = new JobDTO();
        String jobId = FirebaseDatabase.getInstance().getReference().child("jobs").push().getKey();
        jobDTO.setUploaderId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        jobDTO.setId(jobId);
        jobDTO.setCompany(company);
        jobDTO.setDeadline(timeLimit);
        jobDTO.setDescription(detail);
        jobDTO.setLocation(location);
        jobDTO.setTitle(title);
        jobDTO.setSalary(salary);

        uploadJob(jobDTO);

    }

    private void uploadJob(JobDTO job) {

        System.out.println("post data ====="+job.getId());

        progressBar.setVisibility(View.VISIBLE);

        FirebaseDatabase.getInstance().getReference().child("jobs")
                .child(job.getId())
                .setValue(job)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(getWindow().getDecorView().getRootView(), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isComplete()) {

                            progressBar.setVisibility(View.GONE);
//                            onBackPressed();
                            Snackbar.make(getWindow().getDecorView().getRootView(),"Job has been uploaded.", Snackbar.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                });

    }


}
