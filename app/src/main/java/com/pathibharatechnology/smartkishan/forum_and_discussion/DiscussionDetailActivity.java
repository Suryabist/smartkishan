package com.pathibharatechnology.smartkishan.forum_and_discussion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.login_and_signup.UserDTO;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class DiscussionDetailActivity extends AppCompatActivity {

    ImageView postImage;
    TextView name, date, content, comment, likesCount;
    ToggleButton like;
    String uploaderImageText, dateText, contentText, imageUrl, postUploaderUserId, postId;
    String uploaderName;
    Integer likeCount, commentCount;
    CircleImageView uploaderImage;
    RecyclerView commentRecyclerView;
    HashMap<String,Boolean> likes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_detail);

        postImage = findViewById(R.id.postImageID);
        name = findViewById(R.id.postUploaderNameID);
        date = findViewById(R.id.postUploadTimeID);
        content = findViewById(R.id.contentID);
        like = findViewById(R.id.likeID);
        likesCount = findViewById(R.id.likesCountId);
        uploaderImage = findViewById(R.id.imageOfPostUploaderID);
        comment = findViewById(R.id.commentID);
        commentRecyclerView = findViewById(R.id.recyclerViewID);
        likes=new HashMap<>();

        Intent intent = getIntent();
        dateText = intent.getStringExtra("uploadedDate");
        contentText = intent.getStringExtra("content");
        imageUrl = intent.getStringExtra("contentImage");
        postUploaderUserId = intent.getStringExtra("uploaderId");
        postId= intent.getStringExtra("postId");
        commentCount = intent.getIntExtra("commentCount", 0);
        likeCount = intent.getIntExtra("likeCount", 0);
        likes = (HashMap<String, Boolean>) intent.getSerializableExtra("getLikes");

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(postUploaderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserDTO user = dataSnapshot.getValue(UserDTO.class);
                        name.setText(user.getUserName());
                        Glide.with(uploaderImage.getContext())
                                .load(user.getProfilePic())
                                .asBitmap()
                                .into(uploaderImage);

                        Glide.with(postImage.getContext())
                                .load(imageUrl)
                                .asBitmap()
                                .into(postImage);

                        date.setText(dateText);
                        content.setText(contentText);
                        likesCount.setText(likeCount+" likes");
                        if(likes!=null) {
                            if (likes.containsKey(FirebaseAuth.getInstance().getUid())) {
                                if (likes.get(FirebaseAuth.getInstance().getUid())) {
                                    like.setChecked(true);
                                } else {

                                    like.setChecked(false);
                                }
                            }
                        }
                        comment.setText(""+commentCount+" comment");

                        like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                if (b) {
                                    likeCount = likeCount + 1;
                                    sendLike(true);
                                } else {
                                    likeCount = likeCount - 1;
                                    sendLike(false);
                                }
                                likesCount.setText(likeCount+" likes");
                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void sendLike(boolean b) {
        HashMap<String,Boolean> newLikes = new HashMap<>();
        DiscussionDTO discussionDTO = new DiscussionDTO();
        discussionDTO.setDate(dateText);
        discussionDTO.setContent(contentText);
        discussionDTO.setPostId(postId);
        discussionDTO.setCommentCount(commentCount);
        discussionDTO.setImageUrl(imageUrl);
        discussionDTO.setLikeCount(likeCount);
        newLikes.put(FirebaseAuth.getInstance().getUid(), b);
        discussionDTO.setLikes(newLikes);
        discussionDTO.setPostUploaderUserId(postUploaderUserId);

        FirebaseDatabase.getInstance().getReference().child("discussions")
                .child(postId)
                .setValue(discussionDTO);
    }
}
