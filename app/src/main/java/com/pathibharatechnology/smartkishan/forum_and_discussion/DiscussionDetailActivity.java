package com.pathibharatechnology.smartkishan.forum_and_discussion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.forum_and_discussion.comment.CommentAdapter;
import com.pathibharatechnology.smartkishan.forum_and_discussion.comment.CommentDTO;
import com.pathibharatechnology.smartkishan.login_and_signup.UserDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DiscussionDetailActivity extends AppCompatActivity {

    ImageView postImage;
    TextView name, date, content, commentTextView, likesCount;
    ToggleButton like;
    String uploaderImageText, dateText, contentText, imageUrl, postUploaderUserId, postId, commentStr;
    String uploaderName;
    EditText commentText;
    Button sendCommentButton;
    Integer likeCount, commentCount;
    CircleImageView uploaderImage;
    RecyclerView commentRecyclerView;
    HashMap<String,Boolean> likes;
    ImageView optionsBtn;

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
        commentTextView = findViewById(R.id.commentID);
        commentRecyclerView = findViewById(R.id.recyclerViewID);
        commentText = findViewById(R.id.commentTextId);
        sendCommentButton = findViewById(R.id.sendCommentId);
        optionsBtn = findViewById(R.id.optionsId);
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


        commentText.setFocusableInTouchMode(false);

        sendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentStr=commentText.getText().toString();
                if(!TextUtils.isEmpty(commentStr))
                {
                    addCommentToDatabase();
                    commentText.setText("");
                }else{
                    commentText.setError("Required");
                }
            }
        });

        commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentText.setFocusableInTouchMode(true);
            }
        });

        optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContextMenu(view);
            }
        });

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        commentRecyclerView.setLayoutManager(layoutManager);

        getCommentList();

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
                        commentTextView.setText(""+commentCount+" comment");

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

    private void addCommentToDatabase(){
        final CommentDTO comment=new CommentDTO();
        comment.setComment(commentStr);
        comment.setCommeneterId(FirebaseAuth.getInstance().getUid());
        comment.setPostId(postId);

        String commentId=
                FirebaseDatabase.getInstance().getReference().
                        child("comments")
                        .child(postId).push().getKey();

        FirebaseDatabase.getInstance().getReference().
                child("comments")
                .child(postId)
                .child(commentId)
                .setValue(comment)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isComplete()){
                            Toast.makeText(DiscussionDetailActivity.this, "Added to databse", Toast.LENGTH_SHORT).show();
                            FirebaseDatabase.getInstance().getReference().child("discussions")
                                    .child(postId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            DiscussionDTO discussionDTO=dataSnapshot.getValue(DiscussionDTO.class);
                                            discussionDTO.setCommentCount(discussionDTO.getCommentCount()+1);
                                            FirebaseDatabase.getInstance().getReference().child("discussions")
                                                    .child(postId).setValue(discussionDTO);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                        }
                    }
                });

    }


    private void getCommentList(){
        FirebaseDatabase.getInstance().getReference().child("comments")
                .child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<CommentDTO> commentList=new ArrayList<>();
                        Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                        while (iterator.hasNext()){
                            DataSnapshot snap=iterator.next();
                            commentList.add(snap.getValue(CommentDTO.class));

                        }
                        CommentAdapter commentAdapter=new CommentAdapter(commentList);
                        commentRecyclerView.setAdapter(commentAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.option_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.android:
                Toast.makeText(getApplicationContext(),"Android Clicked",Toast.LENGTH_LONG).show();
                return true;

            case R.id.Php:
                Toast.makeText(getApplicationContext(),"Php Clicked",Toast.LENGTH_LONG).show();
                return true;

            case R.id.Blogger:
                Toast.makeText(getApplicationContext(),"Blogger Clicked",Toast.LENGTH_LONG).show();
                return true;

            case R.id.WordPress:
                Toast.makeText(getApplicationContext(),"WordPress Clicked",Toast.LENGTH_LONG).show();
                return true;

            default:

                super.onOptionsItemSelected(item);

        }
        return true;

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
