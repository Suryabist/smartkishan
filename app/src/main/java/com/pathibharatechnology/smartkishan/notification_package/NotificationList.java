package com.pathibharatechnology.smartkishan.notification_package;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.MainDashboardActivity;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.SupportActionBarInitializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class NotificationList extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);
        recyclerView = findViewById(R.id.recyclerViewID);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportActionBarInitializer.setUpSupportActionBar(getSupportActionBar(), "Notifications", true);


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        getNotifications();


    }

    public void getNotifications(){

        FirebaseDatabase.getInstance().getReference().child("notifications").orderByChild("productUploaderUserId").equalTo(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<NotificationDTO> notificationDTOList = new ArrayList<>();
                        List<NotificationDTO> reverseNotificationDTOList = new ArrayList<>();
                        Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                        while (iterator.hasNext()){
                            DataSnapshot snap=iterator.next();
                            notificationDTOList.add(snap.getValue(NotificationDTO.class));
                        }

                        Collections.reverse(notificationDTOList);

                        int size = notificationDTOList.size()-1;

                        for(int i=size;i>=0;i--){
                            reverseNotificationDTOList.add(notificationDTOList.get(i));
                        }

                        NotificationAdapter adapter = new NotificationAdapter(reverseNotificationDTOList, NotificationList.this);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
