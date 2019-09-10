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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.login_and_signup.UserDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DiscussionActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText addDiscussionText;
    ProgressBar progressBar;
    CircleImageView userImage;
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

        FirebaseDatabase.getInstance().getReference().child("discussions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<DiscussionDTO> discussionDTOList = new ArrayList<>();
                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot snap = iterator.next();
                            discussionDTOList.add(snap.getValue(DiscussionDTO.class));

                        }
                        ForumAdapter adapter = new ForumAdapter(discussionDTOList,  progressBar, new OnFeedClickHandleListener() {
                            @Override
                            public void onFeedClicked(DiscussionDTO discussionDTO) {

                                Intent intent = new Intent(DiscussionActivity.this, DiscussionDetailActivity.class);
                                intent.putExtra("userImage", userImageText);
                                intent.putExtra("uploadedDate", discussionDTO.getDate());
                                intent.putExtra("content", discussionDTO.getContent());
                                intent.putExtra("contentImage", discussionDTO.getImageUrl());
                                intent.putExtra("uploaderId", discussionDTO.getPostUploaderUserId());
                                intent.putExtra("postId", discussionDTO.getPostId());
                                intent.putExtra("commentCount", discussionDTO.getCommentCount());
                                intent.putExtra("likeCount", discussionDTO.getLikeCount());
                                intent.putExtra("getLikes", discussionDTO.getLikes());
                                startActivity(intent);

                            }

                            @Override
                            public void onLikeBtnToggled(DiscussionDTO discussionDTO, boolean liked) {
                                HashMap<String,Boolean> likes=new HashMap<>();
                                if(liked){
                                    discussionDTO.setLikeCount(discussionDTO.getLikeCount()+1);
                                    likes.put(FirebaseAuth.getInstance().getUid(),true);
                                    Toast.makeText(DiscussionActivity.this, "Liked", Toast.LENGTH_SHORT).show();
                                }else{
                                    discussionDTO.setLikeCount(discussionDTO.getLikeCount()-1);
                                    likes.put(FirebaseAuth.getInstance().getUid(),false);
                                    Toast.makeText(DiscussionActivity.this, "Unlike", Toast.LENGTH_SHORT).show();
                                }
                                discussionDTO.setLikes(likes);
                                FirebaseDatabase.getInstance().getReference().child("discussions")
                                        .child(discussionDTO.getPostId())
                                        .setValue(discussionDTO);
                            }
                        });
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(layoutManager);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
