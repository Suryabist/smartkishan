package com.pathibharatechnology.smartkishan.forum_and_discussion;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.login_and_signup.UserDTO;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder>{

    List<DiscussionDTO> discussionDTOArrayList;
    ProgressBar progressBar;
    OnFeedClickHandleListener feedClickHandleListener;

    public ForumAdapter(List<DiscussionDTO> discussionDTOArrayList, ProgressBar progressBar, OnFeedClickHandleListener feedClickHandleListener) {
        this.discussionDTOArrayList = discussionDTOArrayList;
        this.progressBar = progressBar;
        this.feedClickHandleListener = feedClickHandleListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_display, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.displayContent(discussionDTOArrayList.get(i));
    }


    @Override
    public int getItemCount() {
        return discussionDTOArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, date, content, comment;
        ToggleButton like;
        String imageText;
        String uploaderName;
        CircleImageView uploaderImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.postImageID);
            name = itemView.findViewById(R.id.postUploaderNameID);
            date = itemView.findViewById(R.id.postUploadTimeID);
            content = itemView.findViewById(R.id.contentID);
            like = itemView.findViewById(R.id.likeID);
            uploaderImage = itemView.findViewById(R.id.imageOfPostUploaderID);
            comment = itemView.findViewById(R.id.commentID);
        }

        public void displayContent(final DiscussionDTO discussionDTO) {
            progressBar.setVisibility(View.VISIBLE);

            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(discussionDTO.getPostUploaderUserId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserDTO user = dataSnapshot.getValue(UserDTO.class);
                            name.setText(user.getUserName());
                            Glide.with(uploaderImage.getContext())
                                    .load(user.getProfilePic())
                                    .asBitmap()
                                    .into(uploaderImage);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            imageText = discussionDTO.getImageUrl();

            if (imageText == null || imageText.equals("")) {
                image.setVisibility(View.GONE);
            } else {
                Glide.with(image.getContext())
                        .load(imageText)
                        .asBitmap()
                        .into(image);
            }

            name.setText(uploaderName);
            date.setText(discussionDTO.getDate());
            content.setText(discussionDTO.getContent());

            comment.setText(discussionDTO.getCommentCount() +" comments");

            if(discussionDTO.getLikes()!=null) {
                if (discussionDTO.getLikes().containsKey(FirebaseAuth.getInstance().getUid())) {
                    if (discussionDTO.getLikes().get(FirebaseAuth.getInstance().getUid())) {
                        like.setChecked(true);
                    } else {

                        like.setChecked(false);
                    }
                }
            }

            like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        System.out.println("Checked=====");
                        feedClickHandleListener.onLikeBtnToggled(discussionDTO,true);
                    }else{

                        System.out.println("Unchecked=====");
                        feedClickHandleListener.onLikeBtnToggled(discussionDTO,false);
                    }
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    feedClickHandleListener.onFeedClicked(discussionDTO);

                }
            });
        }

    }

}
