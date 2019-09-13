package com.pathibharatechnology.smartkishan.user_profile;

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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.login_and_signup.UserDTO;
import com.pathibharatechnology.smartkishan.product_detail.ProductDetailActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailActivity extends AppCompatActivity {

    String userId, userName, userProfilePic, phone;
    TextView joinedDateText, fullNameText, emailText, phoneText;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Button callButton;
    CircleImageView userProfilePicImageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        joinedDateText = findViewById(R.id.joinDateTextviewId);
        fullNameText = findViewById(R.id.userNameTextId);
        emailText = findViewById(R.id.emailTextId);
        phoneText = findViewById(R.id.phoneTextId);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarID);
        callButton = findViewById(R.id.callButtonId);
        userProfilePicImageview = findViewById(R.id.userProfileImageID);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userName = intent.getStringExtra("userName");
        userProfilePic = intent.getStringExtra("userProfilePic");

        getUserDetails();

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(UserDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(UserDetailActivity.this, new String[]{
                            Manifest.permission.CALL_PHONE
                    }, 0);

                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    System.out.println("mobile=====" + phone);
                    callIntent.setData(Uri.parse("tel:" + phone));
                    startActivity(callIntent);
                }
            }
        });


    }

    private void getUserDetails() {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserDTO user=dataSnapshot.getValue(UserDTO.class);

                        collapsingToolbarLayout.setTitle(user.getUserName());
                        Glide.with(userProfilePicImageview.getContext())
                                .load(userProfilePic)
                                .asBitmap()
                                .into(userProfilePicImageview);
                        phone = user.getMobile();
                        joinedDateText.setText("Joined on "+user.getJoinedTime());
                        fullNameText.setText(user.getFullName());
                        emailText.setText(user.getEmail());
                        phoneText.setText(phone);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
