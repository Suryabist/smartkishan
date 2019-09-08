package com.pathibharatechnology.smartkishan.job;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.login_and_signup.UserDTO;
import com.pathibharatechnology.smartkishan.product_detail.ProductDetailActivity;
import com.pathibharatechnology.smartkishan.user_profile.UserProfileActivity;

public class JobDetailActivity extends AppCompatActivity {

    String company, title, location, expiryDate, description, uploaderId, postedDate, uploaderName, uploaderProfilePic;
    String mobile, email, clientMobile;
    Long salary;
    LinearLayout uploaderProfileLayout;
    FloatingActionButton callJobUploader;
    Button applyButton;
    TextView titleText, companyText, locationText, salaryText, deadlineText, jobDetailText, uploaderText, postedDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        titleText = findViewById(R.id.titleTextId);
        companyText = findViewById(R.id.companyTextId);
        locationText = findViewById(R.id.locationTextId);
        salaryText = findViewById(R.id.salaryTextId);
        deadlineText = findViewById(R.id.deadlineTextId);
        jobDetailText = findViewById(R.id.detailTextId);
        uploaderText = findViewById(R.id.uploaderId);
        postedDateText = findViewById(R.id.postedTextId);
        uploaderProfileLayout = findViewById(R.id.uploaderDetailID);
        callJobUploader = findViewById(R.id.callFloatingButtonID);
        applyButton = findViewById(R.id.applyButtonId);

        Intent intent = getIntent();
        company = intent.getStringExtra("company");
        title = intent.getStringExtra("title");
        location = intent.getStringExtra("location");
        expiryDate = intent.getStringExtra("expiryDate");
        description = intent.getStringExtra("description");
        uploaderName = intent.getStringExtra("uploaderName");
        uploaderId = intent.getStringExtra("postedById");
        uploaderProfilePic = intent.getStringExtra("uploaderProfilePic");
        postedDate = intent.getStringExtra("postedDate");
        mobile = intent.getStringExtra("mobile");
        salary = intent.getLongExtra("salary", 0);

        getUserDetails();
        getUploaderPhone(uploaderId);


        titleText.setText(title);
        companyText.setText(company);
        locationText.setText(location);
        salaryText.setText(""+salary);
        deadlineText.setText(expiryDate);
        jobDetailText.setText(description);
        uploaderText.setText(uploaderName);
        postedDateText.setText(postedDate);

        uploaderProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userDetailIntent = new Intent(JobDetailActivity.this, UserProfileActivity.class);
                userDetailIntent.putExtra("userId", uploaderId);
                userDetailIntent.putExtra("userName", uploaderName);
                userDetailIntent.putExtra("userProfilePic", uploaderProfilePic);
                startActivity(userDetailIntent);
                finish();
            }
        });


        callJobUploader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(JobDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(JobDetailActivity.this, new String[]{
                            Manifest.permission.CALL_PHONE
                    }, 0);

                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    System.out.println("mobile=====" + mobile);
                    callIntent.setData(Uri.parse("tel:" + mobile));
                    startActivity(callIntent);
                }
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMailToUploader();
            }
        });
    }

    private void getUploaderPhone(final String uploaderUserId) {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(uploaderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserDTO user = dataSnapshot.getValue(UserDTO.class);
                        mobile = user.getMobile();
                        email = user.getEmail();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void getUserDetails() {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserDTO user = dataSnapshot.getValue(UserDTO.class);
                        clientMobile = user.getMobile();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void sendMailToUploader() {


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (email != null && !email.equals("Not Available")) {
                Intent intent = new Intent("android.intent.action.SENDTO", Uri.fromParts("mailto", email, null));
                intent.putExtra("android.intent.extra.SUBJECT", "Application For Job");
                if (clientMobile != null && !clientMobile.equals("Not Available")) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("I saw your job vacancy in Smart Kishan. I think I am the best fit for that position. Please feel free to contact me for further processing. You can also call me at ");
                    stringBuilder.append(clientMobile);
                    intent.putExtra("android.intent.extra.TEXT", stringBuilder.toString());
                } else {
                    intent.putExtra("android.intent.extra.TEXT", "I want to apply for this post, Please reply to this mail.");
                }
                this.startActivity(intent);
            } else {
                Toast.makeText(this, "Sorry, Uploader does not provide us his/her email", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Please login first.", Toast.LENGTH_SHORT).show();
        }
    }

}
