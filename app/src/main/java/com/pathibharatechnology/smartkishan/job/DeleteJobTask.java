package com.pathibharatechnology.smartkishan.job;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.pathibharatechnology.smartkishan.MainDashboardActivity;
import com.pathibharatechnology.smartkishan.product_by_category.ProductListDTO;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class DeleteJobTask {

    JobDTO jobDTO;

    public void deleteJob(String jobId, final Context context) {

        jobDTO = new JobDTO();

        FirebaseDatabase.getInstance().getReference().child("jobs")
                .child(jobId)
                .setValue(jobDTO)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "काम मेटाउन सक्दैन।", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isComplete()) {
                            Toast.makeText(context, "काम सफलतापूर्वक हटाइयो।", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
}
