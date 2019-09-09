package com.pathibharatechnology.smartkishan.forum_and_discussion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class DiscussionActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText addDiscussionText;
    ProgressBar progressBar;
    CircleImageView userImage;
    ForumAdapter adapter;
    String userImageText;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        addDiscussionText = findViewById(R.id.addDiscussionId);
        progressBar = findViewById(R.id.progressBarId);
        userImage = findViewById(R.id.userPicID);

        Intent intent = getIntent();
        userImageText = intent.getStringExtra("userImage");

        Glide.with(this)
                .load(userImageText)
                .asBitmap()
                .into(userImage);

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

    private void getDiscussionListFromFirebase() {

        progressBar.setVisibility(View.VISIBLE);
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
                        adapter = new ForumAdapter(discussionDTOList, DiscussionActivity.this, progressBar);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(layoutManager);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
