package com.pathibharatechnology.smartkishan.login;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.MainDashboardActivity;
import com.pathibharatechnology.smartkishan.login.login_view.IVLogin;
import com.pathibharatechnology.smartkishan.login_and_signup.UserDTO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetUserDetailFromFirebase {

    IVLogin ivLogin;

    public GetUserDetailFromFirebase(IVLogin ivLogin) {
        this.ivLogin = ivLogin;
    }

    public void checkForUserDetail(String userId) {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final UserDTO user = dataSnapshot.getValue(UserDTO.class);
                        ivLogin.parseUserDetail(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

}
