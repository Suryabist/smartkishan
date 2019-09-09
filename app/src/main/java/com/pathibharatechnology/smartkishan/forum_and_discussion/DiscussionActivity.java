package com.pathibharatechnology.smartkishan.forum_and_discussion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.login_and_signup.UserDTO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DiscussionActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText addDiscussionText;

    ForumAdapter adapter;
    LinearLayoutManager layoutManager;
    String uploaderName, uploaderImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        getUserDetails();
        addDiscussionText = findViewById(R.id.addDiscussionId);

        recyclerView = findViewById(R.id.recyclerViewID);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        getDiscussionListFromFirebase();


        addDiscussionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiscussionActivity.this, AddDiscussionPostActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    public void getUserDetails() {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserDTO user = dataSnapshot.getValue(UserDTO.class);
                        uploaderName = user.getUserName();
                        uploaderImage = user.getProfilePic();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void getDiscussionListFromFirebase() {

        final List<DiscussionDTO> discussionDTOList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("discussions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot snap = iterator.next();
                            discussionDTOList.add(snap.getValue(DiscussionDTO.class));

                        }
                        adapter = new ForumAdapter(discussionDTOList, DiscussionActivity.this, uploaderName, uploaderImage);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(layoutManager);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
